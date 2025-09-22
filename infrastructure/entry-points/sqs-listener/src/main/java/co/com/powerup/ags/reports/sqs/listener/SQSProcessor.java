package co.com.powerup.ags.reports.sqs.listener;

import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.AddLoanApprovedCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    
    private final ApprovedLoanReportUseCase approvedLoanReportUseCase;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Message received with id: {} and body {}:", message.messageId(), message.body());
        
        AddLoanApprovedCommand command = getUpdateLoanAutomaticValidationCommand(message);
        
        return approvedLoanReportUseCase.processApprovedLoan(command);
    }
    
    private static AddLoanApprovedCommand getUpdateLoanAutomaticValidationCommand(Message message) {
        try {
            JsonNode response = objectMapper.readTree(message.body());
            return new AddLoanApprovedCommand(response.get("loanId").asText(),
                    new BigDecimal(response.get("amount").asText()),
                    Instant.parse(response.get("approvedDate").asText()));
        } catch (JsonProcessingException e) {
            log.info("Error mapping message response with id {} and body {}", message.messageId(), message.body());
            throw new RuntimeException(e);
        }
    }
}
