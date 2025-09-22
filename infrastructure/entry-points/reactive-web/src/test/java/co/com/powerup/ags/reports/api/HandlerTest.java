package co.com.powerup.ags.reports.api;

import co.com.powerup.ags.reports.api.dto.ApprovedLoanGlobalSummary;
import co.com.powerup.ags.reports.api.dto.SuccessResponse;
import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private ApprovedLoanReportUseCase approvedLoanReportUseCase;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private Handler handler;

    private co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary mockUseCaseResponse;

    @BeforeEach
    void setUp() {
        mockUseCaseResponse = new co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary(150L, new BigDecimal("2500000.50"));
    }

    @Test
    void shouldReturnSuccessResponseWhenGetApprovedGlobalSummary() {
        when(serverRequest.path()).thenReturn("/api/v1/reports");
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(mockUseCaseResponse));

        StepVerifier.create(handler.getApprovedGlobalSummary(serverRequest))
                .assertNext(response -> {
                    assert response.statusCode().is2xxSuccessful();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnCorrectContentTypeWhenGetApprovedGlobalSummary() {
        when(serverRequest.path()).thenReturn("/api/v1/reports");
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(mockUseCaseResponse));

        StepVerifier.create(handler.getApprovedGlobalSummary(serverRequest))
                .assertNext(response -> {
                    assert response.headers().getContentType().equals(MediaType.APPLICATION_JSON);
                })
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenUseCaseFails() {
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.error(new RuntimeException("Use case error")));

        StepVerifier.create(handler.getApprovedGlobalSummary(serverRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleEmptyResponseFromUseCase() {
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.empty());

        StepVerifier.create(handler.getApprovedGlobalSummary(serverRequest))
                .assertNext(response -> {
                    assert response.statusCode().value() == 204;
                })
                .verifyComplete();
    }

    @Test
    void shouldMapUseCaseResponseCorrectlyToApiResponse() {
        when(serverRequest.path()).thenReturn("/api/v1/reports");
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(mockUseCaseResponse));

        handler.getApprovedGlobalSummary(serverRequest)
                .cast(org.springframework.web.reactive.function.server.EntityResponse.class)
                .subscribe(response -> {
                    SuccessResponse<ApprovedLoanGlobalSummary> body = 
                        (SuccessResponse<ApprovedLoanGlobalSummary>) response.entity();
                    
                    assert body.getData().getTotalCount().equals(150L);
                    assert body.getData().getTotalAmount().equals(new BigDecimal("2500000.50"));
                    assert body.getMessage().equals("Stats returned successfully");
                    assert body.getPath().equals("/api/v1/reports");
                });
    }

    @Test
    void shouldIncludeTimestampInResponse() {
        when(serverRequest.path()).thenReturn("/api/v1/reports");
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(mockUseCaseResponse));

        handler.getApprovedGlobalSummary(serverRequest)
                .cast(org.springframework.web.reactive.function.server.EntityResponse.class)
                .subscribe(response -> {
                    SuccessResponse<ApprovedLoanGlobalSummary> body = 
                        (SuccessResponse<ApprovedLoanGlobalSummary>) response.entity();
                    
                    assert body.getTimestamp() != null;
                });
    }

    @Test
    void shouldHandleZeroValuesInUseCaseResponse() {
        when(serverRequest.path()).thenReturn("/api/v1/reports");
        co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary zeroValuesResponse = 
            new co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary(0L, BigDecimal.ZERO);
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(zeroValuesResponse));

        StepVerifier.create(handler.getApprovedGlobalSummary(serverRequest))
                .assertNext(response -> {
                    assert response.statusCode().is2xxSuccessful();
                })
                .verifyComplete();
    }
}