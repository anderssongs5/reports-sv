package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.dynamodb.helper.TemplateAdapterOperations;
import co.com.powerup.ags.reports.model.approvedloanreport.GlobalSummaryReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.GlobalSummaryReportRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Repository
public class DynamoDBGlobalSummaryTemplateAdapter extends
        TemplateAdapterOperations<GlobalSummaryReport, String, GlobalSummaryEntity>
        implements GlobalSummaryReportRepository {

    private static final Logger log = LoggerFactory.getLogger(DynamoDBGlobalSummaryTemplateAdapter.class);

    public DynamoDBGlobalSummaryTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> new GlobalSummaryReport(d.getTotalCount(), d.getTotalAmount()),
                "global-summary-report");
    }

    public Mono<List<GlobalSummaryReport>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<GlobalSummaryReport>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "secondary_index" /*index is optional if you define in constructor*/);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }
    
    @Override
    public Mono<GlobalSummaryReport> getGlobalSummary(String loanStatus) {
        String key = "LOAN_STATUS#" + loanStatus.toUpperCase();
        log.info("Getting global summary for loanStatus: {}, using key: {}", loanStatus, key);
        
        return this.getById(key)
                .doOnNext(result -> log.info("Global summary found: {}", result))
                .doOnError(error -> log.error("Error getting global summary for key: {}", key, error))
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("No global summary found for key: {}", key)));
    }
}
