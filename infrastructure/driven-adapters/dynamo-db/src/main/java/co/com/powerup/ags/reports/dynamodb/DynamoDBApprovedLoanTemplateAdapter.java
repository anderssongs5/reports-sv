package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.dynamodb.helper.TemplateAdapterOperations;
import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.ApprovedLoanReportRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Repository
public class DynamoDBApprovedLoanTemplateAdapter extends TemplateAdapterOperations<ApprovedLoanReport, String, ApprovedLoanEntity> implements ApprovedLoanReportRepository {

    public DynamoDBApprovedLoanTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, ApprovedLoanReport.class),
                "approved-loans-reports");
    }

    public Mono<List<ApprovedLoanReport>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<ApprovedLoanReport>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
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
    public Mono<ApprovedLoanReport> saveApprovedLoan(ApprovedLoanReport approvedLoanReport) {
        return save(approvedLoanReport);
    }
}
