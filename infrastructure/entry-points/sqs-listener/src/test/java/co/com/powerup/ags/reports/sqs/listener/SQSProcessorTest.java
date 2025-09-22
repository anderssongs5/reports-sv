package co.com.powerup.ags.reports.sqs.listener;

import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.AddLoanApprovedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSProcessorTest {

    @Mock
    private ApprovedLoanReportUseCase approvedLoanReportUseCase;

    @InjectMocks
    private SQSProcessor sqsProcessor;

    private Message validMessage;
    private final String VALID_MESSAGE_BODY = """
            {
                "loanId": "loan-123",
                "amount": "50000.75",
                "approvedDate": "2025-09-21T15:30:00Z"
            }
            """;

    @BeforeEach
    void setUp() {
        validMessage = Message.builder()
                .messageId("test-message-id")
                .body(VALID_MESSAGE_BODY)
                .build();
    }

    @Test
    void shouldProcessValidMessageSuccessfully() {
        when(approvedLoanReportUseCase.processApprovedLoan(any(AddLoanApprovedCommand.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(sqsProcessor.apply(validMessage))
                .verifyComplete();

        verify(approvedLoanReportUseCase).processApprovedLoan(
                argThat(command -> 
                    command.loanId().equals("loan-123") &&
                    command.amount().equals(new BigDecimal("50000.75")) &&
                    command.approvedDate().equals(Instant.parse("2025-09-21T15:30:00Z"))
                )
        );
    }

    @Test
    void shouldPropagateErrorWhenUseCaseFails() {
        RuntimeException useCaseError = new RuntimeException("UseCase failed");
        when(approvedLoanReportUseCase.processApprovedLoan(any(AddLoanApprovedCommand.class)))
                .thenReturn(Mono.error(useCaseError));

        StepVerifier.create(sqsProcessor.apply(validMessage))
                .expectError(RuntimeException.class)
                .verify();
    }
    
    @Test
    void shouldHandleValidMessageWithDifferentAmountFormat() {
        Message messageWithIntegerAmount = Message.builder()
                .messageId("integer-amount-message")
                .body("""
                        {
                            "loanId": "loan-456",
                            "amount": "100000",
                            "approvedDate": "2025-09-22T10:00:00Z"
                        }
                        """)
                .build();

        when(approvedLoanReportUseCase.processApprovedLoan(any(AddLoanApprovedCommand.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(sqsProcessor.apply(messageWithIntegerAmount))
                .verifyComplete();

        verify(approvedLoanReportUseCase).processApprovedLoan(
                argThat(command -> 
                    command.loanId().equals("loan-456") &&
                    command.amount().equals(new BigDecimal("100000")) &&
                    command.approvedDate().equals(Instant.parse("2025-09-22T10:00:00Z"))
                )
        );
    }
}