package co.com.powerup.ags.reports.dynamodb;

import co.com.powerup.ags.reports.model.approvedloanreport.GlobalSummaryReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamoDBGlobalSummaryTemplateAdapterTest {

    private DynamoDBGlobalSummaryTemplateAdapter adapter;
    private GlobalSummaryReport testReport;

    @BeforeEach
    void setUp() {
        DynamoDbEnhancedAsyncClient mockClient = mock(DynamoDbEnhancedAsyncClient.class);
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        
        // Create a spy adapter that we can mock specific methods on
        adapter = spy(new DynamoDBGlobalSummaryTemplateAdapter(mockClient, mockMapper));
        
        testReport = new GlobalSummaryReport(50L, new BigDecimal("1000000.50"));
    }

    @Test
    void shouldGetGlobalSummaryWhenRecordExists() {
        // Mock the getById method which is called internally by getGlobalSummary
        doReturn(Mono.just(testReport))
                .when(adapter).getById("LOAN_STATUS#APPROVED");

        StepVerifier.create(adapter.getGlobalSummary("APPROVED"))
                .assertNext(summary -> {
                    assert summary.totalCount().equals(50L);
                    assert summary.totalAmount().equals(new BigDecimal("1000000.50"));
                })
                .verifyComplete();
        
        // Verify the correct key was used
        verify(adapter).getById("LOAN_STATUS#APPROVED");
    }

    @Test
    void shouldReturnEmptyWhenRecordDoesNotExist() {
        doReturn(Mono.empty())
                .when(adapter).getById("LOAN_STATUS#APPROVED");

        StepVerifier.create(adapter.getGlobalSummary("APPROVED"))
                .verifyComplete();
        
        verify(adapter).getById("LOAN_STATUS#APPROVED");
    }

    @Test
    void shouldPropagateErrorWhenDynamoDbFails() {
        RuntimeException testException = new RuntimeException("DynamoDB error");
        doReturn(Mono.error(testException))
                .when(adapter).getById("LOAN_STATUS#APPROVED");

        StepVerifier.create(adapter.getGlobalSummary("APPROVED"))
                .expectError(RuntimeException.class)
                .verify();
        
        verify(adapter).getById("LOAN_STATUS#APPROVED");
    }

    @Test
    void shouldUseCorrectKeyWhenGettingGlobalSummary() {
        doReturn(Mono.just(testReport))
                .when(adapter).getById("LOAN_STATUS#APPROVED");

        // Test with lowercase input - should be converted to uppercase in key
        StepVerifier.create(adapter.getGlobalSummary("approved"))
                .expectNextCount(1)
                .verifyComplete();
        
        // Verify the key was correctly formatted with uppercase
        verify(adapter).getById("LOAN_STATUS#APPROVED");
    }

    @Test
    void shouldHandleUppercaseConversionWhenGettingGlobalSummary() {
        doReturn(Mono.just(testReport))
                .when(adapter).getById("LOAN_STATUS#APPROVED");

        // Test different case variations - all should result in the same key
        adapter.getGlobalSummary("approved").subscribe();
        adapter.getGlobalSummary("APPROVED").subscribe();
        adapter.getGlobalSummary("Approved").subscribe();
        
        // All calls should use the same uppercase key
        verify(adapter, times(3)).getById("LOAN_STATUS#APPROVED");
    }

    @Test
    void shouldHandleEmptyLoanStatus() {
        doReturn(Mono.just(testReport))
                .when(adapter).getById("LOAN_STATUS#");

        StepVerifier.create(adapter.getGlobalSummary(""))
                .expectNextCount(1)
                .verifyComplete();
        
        verify(adapter).getById("LOAN_STATUS#");
    }
}