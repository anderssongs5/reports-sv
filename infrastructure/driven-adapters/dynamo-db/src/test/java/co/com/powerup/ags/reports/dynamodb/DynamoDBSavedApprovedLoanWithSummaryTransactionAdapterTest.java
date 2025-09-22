package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactionConflictException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDBSavedApprovedLoanWithSummaryTransactionAdapterTest {

    @Mock
    private DynamoDbAsyncClient dynamoDbAsyncClient;

    @InjectMocks
    private DynamoDBSavedApprovedLoanWithSummaryTransactionAdapter adapter;

    private ApprovedLoanReport testLoanReport;

    @BeforeEach
    void setUp() {
        testLoanReport = ApprovedLoanReport.builder()
                .loanId("loan-123")
                .amount(new BigDecimal("50000.75"))
                .approvedDate(Instant.parse("2025-09-21T15:30:00Z"))
                .build();
    }

    @Test
    void shouldSaveApprovedLoanWithSummaryUpdateWhenValidReport() {
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(adapter.saveApprovedLoanWithSummaryUpdate(testLoanReport))
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenDynamoDbFails() {
        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DynamoDB error")));

        StepVerifier.create(adapter.saveApprovedLoanWithSummaryUpdate(testLoanReport))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleZeroAmountCorrectly() {
        ApprovedLoanReport reportWithZeroAmount = ApprovedLoanReport.builder()
                .loanId("loan-789")
                .amount(BigDecimal.ZERO)
                .approvedDate(Instant.now())
                .build();

        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(adapter.saveApprovedLoanWithSummaryUpdate(reportWithZeroAmount))
                .verifyComplete();
    }

    @Test
    void shouldHandleLargeAmountCorrectly() {
        ApprovedLoanReport reportWithLargeAmount = ApprovedLoanReport.builder()
                .loanId("loan-999")
                .amount(new BigDecimal("999999999.99"))
                .approvedDate(Instant.now())
                .build();

        when(dynamoDbAsyncClient.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(TransactWriteItemsResponse.builder().build()));

        StepVerifier.create(adapter.saveApprovedLoanWithSummaryUpdate(reportWithLargeAmount))
                .verifyComplete();
    }
}