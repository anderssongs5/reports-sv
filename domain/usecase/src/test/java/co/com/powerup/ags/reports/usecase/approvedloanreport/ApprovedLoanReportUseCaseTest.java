package co.com.powerup.ags.reports.usecase.approvedloanreport;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.GlobalSummaryReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.ApprovedLoanTransactionRepository;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.GlobalSummaryReportRepository;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.AddLoanApprovedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovedLoanReportUseCaseTest {
    
    private static final String LOAN_ID = "loan-123";
    private static final BigDecimal AMOUNT = new BigDecimal("50000.75");
    private static final Instant APPROVED_DATE = Instant.parse("2025-09-21T15:30:00Z");
    private static final String APPROVED = "APPROVED";

    @Mock
    private ApprovedLoanTransactionRepository transactionRepository;

    @Mock
    private GlobalSummaryReportRepository globalSummaryReportRepository;

    @InjectMocks
    private ApprovedLoanReportUseCase approvedLoanReportUseCase;

    private AddLoanApprovedCommand validCommand;
    private GlobalSummaryReport validGlobalSummary;

    @BeforeEach
    void setUp() {
        validCommand = new AddLoanApprovedCommand(LOAN_ID, AMOUNT, APPROVED_DATE);
        validGlobalSummary = new GlobalSummaryReport(100L, new BigDecimal("2500000.50"));
    }

    @Test
    void shouldProcessApprovedLoanWhenValidCommandProvided() {
        when(transactionRepository.saveApprovedLoanWithSummaryUpdate(any(ApprovedLoanReport.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(approvedLoanReportUseCase.processApprovedLoan(validCommand))
                .verifyComplete();

        verify(transactionRepository).saveApprovedLoanWithSummaryUpdate(any(ApprovedLoanReport.class));
    }

    @Test
    void shouldCreateLoanReportWithCorrectDataWhenProcessingApprovedLoan() {
        when(transactionRepository.saveApprovedLoanWithSummaryUpdate(any(ApprovedLoanReport.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(approvedLoanReportUseCase.processApprovedLoan(validCommand))
                .verifyComplete();

        verify(transactionRepository).saveApprovedLoanWithSummaryUpdate(
                argThat(loanReport -> 
                    loanReport.getLoanId().equals(LOAN_ID) &&
                    loanReport.getAmount().equals(AMOUNT) &&
                    loanReport.getApprovedDate().equals(APPROVED_DATE)
                )
        );
    }

    @Test
    void shouldPropagateErrorWhenTransactionRepositoryFails() {
        RuntimeException expectedException = new RuntimeException("Database connection failed");
        when(transactionRepository.saveApprovedLoanWithSummaryUpdate(any(ApprovedLoanReport.class)))
                .thenReturn(Mono.error(expectedException));

        StepVerifier.create(approvedLoanReportUseCase.processApprovedLoan(validCommand))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldRetrieveGlobalSummaryWhenDataExists() {
        when(globalSummaryReportRepository.getGlobalSummary(APPROVED))
                .thenReturn(Mono.just(validGlobalSummary));

        StepVerifier.create(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .assertNext(summary -> {
                    assert summary.totalCount().equals(100L);
                    assert summary.totalAmount().equals(new BigDecimal("2500000.50"));
                })
                .verifyComplete();

        verify(globalSummaryReportRepository).getGlobalSummary(APPROVED);
    }

    @Test
    void shouldMapGlobalSummaryCorrectlyWhenConvertingToResponseDTO() {
        when(globalSummaryReportRepository.getGlobalSummary(APPROVED))
                .thenReturn(Mono.just(validGlobalSummary));

        StepVerifier.create(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .assertNext(summary -> {
                    assert summary.totalCount().equals(validGlobalSummary.totalCount());
                    assert summary.totalAmount().equals(validGlobalSummary.totalAmount());
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleEmptyResultWhenNoGlobalSummaryExists() {
        when(globalSummaryReportRepository.getGlobalSummary(APPROVED))
                .thenReturn(Mono.empty());

        StepVerifier.create(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenGlobalSummaryRepositoryFails() {
        RuntimeException expectedException = new RuntimeException("Failed to retrieve global summary");
        when(globalSummaryReportRepository.getGlobalSummary(APPROVED))
                .thenReturn(Mono.error(expectedException));

        StepVerifier.create(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldUseCorrectLoanStatusConstantWhenRetrievingGlobalSummary() {
        when(globalSummaryReportRepository.getGlobalSummary(eq(APPROVED)))
                .thenReturn(Mono.just(validGlobalSummary));

        StepVerifier.create(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .expectNextCount(1)
                .verifyComplete();

        verify(globalSummaryReportRepository).getGlobalSummary(APPROVED);
    }

}