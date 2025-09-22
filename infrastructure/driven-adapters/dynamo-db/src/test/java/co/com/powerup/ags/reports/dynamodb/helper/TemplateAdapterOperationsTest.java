package co.com.powerup.ags.reports.dynamodb.helper;

import co.com.powerup.ags.reports.dynamodb.DynamoDBApprovedLoanTemplateAdapter;
import co.com.powerup.ags.reports.dynamodb.ApprovedLoanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<ApprovedLoanEntity> customerTable;

    private ApprovedLoanEntity approvedLoanEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table("table_name", TableSchema.fromBean(ApprovedLoanEntity.class)))
                .thenReturn(customerTable);

        approvedLoanEntity = new ApprovedLoanEntity();
        approvedLoanEntity.setPk("id");
        approvedLoanEntity.setAmount("atr1");
    }

    @Test
    void modelEntityPropertiesMustNotBeNull() {
        ApprovedLoanEntity approvedLoanEntityUnderTest = new ApprovedLoanEntity("id", "atr1");

        assertNotNull(approvedLoanEntityUnderTest.getPk());
        assertNotNull(approvedLoanEntityUnderTest.getAmount());
    }

    @Test
    void testSave() {
        when(customerTable.putItem(approvedLoanEntity)).thenReturn(CompletableFuture.runAsync(()->{}));
        when(mapper.map(approvedLoanEntity, ApprovedLoanEntity.class)).thenReturn(approvedLoanEntity);

        DynamoDBApprovedLoanTemplateAdapter dynamoDBApprovedLoanTemplateAdapter =
                new DynamoDBApprovedLoanTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBApprovedLoanTemplateAdapter.save(approvedLoanEntity))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        String id = "id";

        when(customerTable.getItem(
                Key.builder().partitionValue(AttributeValue.builder().s(id).build()).build()))
                .thenReturn(CompletableFuture.completedFuture(approvedLoanEntity));
        when(mapper.map(approvedLoanEntity, Object.class)).thenReturn("value");

        DynamoDBApprovedLoanTemplateAdapter dynamoDBApprovedLoanTemplateAdapter =
                new DynamoDBApprovedLoanTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBApprovedLoanTemplateAdapter.getById("id"))
                .expectNext("value")
                .verifyComplete();
    }

    @Test
    void testDelete() {
        when(mapper.map(approvedLoanEntity, ApprovedLoanEntity.class)).thenReturn(approvedLoanEntity);
        when(mapper.map(approvedLoanEntity, Object.class)).thenReturn("value");

        when(customerTable.deleteItem(approvedLoanEntity))
                .thenReturn(CompletableFuture.completedFuture(approvedLoanEntity));

        DynamoDBApprovedLoanTemplateAdapter dynamoDBApprovedLoanTemplateAdapter =
                new DynamoDBApprovedLoanTemplateAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(dynamoDBApprovedLoanTemplateAdapter.delete(approvedLoanEntity))
                .expectNext("value")
                .verifyComplete();
    }
}