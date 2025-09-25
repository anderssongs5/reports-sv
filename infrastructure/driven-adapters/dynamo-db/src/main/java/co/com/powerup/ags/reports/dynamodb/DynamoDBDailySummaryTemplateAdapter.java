package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.dynamodb.helper.TemplateAdapterOperations;
import co.com.powerup.ags.reports.model.approvedloanreport.DailySummaryReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.DailySummaryReportRepository;
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
public class DynamoDBDailySummaryTemplateAdapter extends
        TemplateAdapterOperations<DailySummaryReport, String, DailySummaryEntity>
        implements DailySummaryReportRepository {
    
    private static final Logger log = LoggerFactory.getLogger(DynamoDBDailySummaryTemplateAdapter.class);

    public DynamoDBDailySummaryTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, DailySummaryReport.class),
                "daily-loan-summaries");
    }

    public Mono<List<DailySummaryReport>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<DailySummaryReport>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
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
    public Mono<DailySummaryReport> getDailySummary(String date) {
        String key = "DATE#" + date;
        log.info("Getting daily summary for date: {}, using key: {}", date, key);
        
        return this.getById(key)
                .doOnNext(result -> log.info("Daily summary found: {}", result))
                .doOnError(error -> log.error("Error getting daily summary for key: {}", key, error))
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("No daily summary found for key: {}", key)));
    }
}
