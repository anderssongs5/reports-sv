package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.ApprovedLoanTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.TransactionConflictException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DynamoDBSavedApprovedLoanWithSummaryTransactionAdapter
        implements ApprovedLoanTransactionRepository {

    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private static final Logger log = LoggerFactory.getLogger(DynamoDBSavedApprovedLoanWithSummaryTransactionAdapter.class);

    @Override
    public Mono<Void> saveApprovedLoanWithSummaryUpdate(ApprovedLoanReport approvedLoanReport) {
        log.info("Inserting and updating report information: {}", approvedLoanReport);
        
        ApprovedLoanEntity loanEntity = ApprovedLoanEntity.fromMessage(
            approvedLoanReport.getLoanId(),
            approvedLoanReport.getAmount(),
            approvedLoanReport.getApprovedDate()
        );

        LocalDate approvedDate = LocalDate.ofInstant(approvedLoanReport.getApprovedDate(), ZoneOffset.UTC);
        
        String dateKey = "DATE#" + approvedDate.toString();
        
        TransactWriteItem putLoanItem = TransactWriteItem.builder()
                .put(builder -> builder
                        .tableName("approved-loans-reports")
                        .item(Map.of(
                                "PK", AttributeValue.builder().s(loanEntity.getPk()).build(),
                                "GSI1PK", AttributeValue.builder().s(loanEntity.getGsi1PK()).build(),
                                "GSI1SK", AttributeValue.builder().s(loanEntity.getGsi1SK()).build(),
                                "loanId", AttributeValue.builder().s(loanEntity.getLoanId()).build(),
                                "amount", AttributeValue.builder().n(loanEntity.getAmount().toString()).build(),
                                "approvedDate", AttributeValue.builder().s(loanEntity.getApprovedDate().toString()).build()
                        ))
                        .build())
                .build();

        TransactWriteItem updateSummaryItem = TransactWriteItem.builder()
                .update(builder -> builder
                        .tableName("daily-loan-summaries")
                        .key(Map.of("PK", AttributeValue.builder().s(dateKey).build()))
                        .updateExpression("SET totalCount = if_not_exists(totalCount, :zero) + :inc, " +
                                "totalAmount = if_not_exists(totalAmount, :zeroDecimal) + :amount, " +
                                "lastUpdated = :timestamp, " +
                                "GSI1PK = if_not_exists(GSI1PK, :monthKey), " +
                                "GSI1SK = if_not_exists(GSI1SK, :dayKey)")
                        .expressionAttributeValues(Map.of(
                                ":inc", AttributeValue.builder().n("1").build(),
                                ":amount", AttributeValue.builder().n(approvedLoanReport.getAmount().toString()).build(),
                                ":timestamp", AttributeValue.builder().s(Instant.now().toString()).build(),
                                ":monthKey", AttributeValue.builder().s( "MONTH#" + approvedDate.toString().substring(0, 7)).build(),
                                ":dayKey", AttributeValue.builder().s( "DAY#" + String.format("%02d", approvedDate.getDayOfMonth())).build(),
                                ":zero", AttributeValue.builder().n("0").build(),
                                ":zeroDecimal", AttributeValue.builder().n("0").build()
                        ))
                        .build())
                .build();
        
        TransactWriteItem updateGlobalSummary = TransactWriteItem.builder()
                .update(builder -> builder
                        .tableName("global-summary-report")
                        .key(Map.of("PK", AttributeValue.builder().s("LOAN_STATUS#APPROVED").build()))
                        .updateExpression("SET totalCount = if_not_exists(totalCount, :zero) + :inc, " +
                                "totalAmount = if_not_exists(totalAmount, :zeroDecimal) + :amount, " +
                                "lastUpdated = :timestamp")
                        .expressionAttributeValues(Map.of(
                                ":inc", AttributeValue.builder().n("1").build(),
                                ":amount", AttributeValue.builder().n(approvedLoanReport.getAmount().toString()).build(),
                                ":timestamp", AttributeValue.builder().s(Instant.now().toString()).build(),
                                ":zero", AttributeValue.builder().n("0").build(),
                                ":zeroDecimal", AttributeValue.builder().n("0").build()
                        ))
                        .build())
                .build();

        TransactWriteItemsRequest request = TransactWriteItemsRequest.builder()
                .transactItems(List.of(putLoanItem, updateSummaryItem, updateGlobalSummary))
                .build();

        return Mono.fromFuture(dynamoDbAsyncClient.transactWriteItems(request))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                    .maxBackoff(Duration.ofSeconds(3))
                    .filter(throwable -> {
                        log.error("Error inserting and updating report information: {}", approvedLoanReport, throwable);
                        return throwable instanceof TransactionConflictException;
                    }))
                .doOnNext(response ->
                        log.info("Successfully inserted/updated report information: {}. Response: {}",
                                approvedLoanReport, response))
                .then();
    }
}