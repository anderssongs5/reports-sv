package co.com.powerup.ags.reports.api;

import co.com.powerup.ags.reports.api.dto.SuccessResponse;
import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ApprovedLoanReportUseCase approvedLoanReportUseCase;
    
    public Mono<ServerResponse> getApprovedGlobalSummary(ServerRequest serverRequest) {
        return approvedLoanReportUseCase.getApprovedLoanGlobalSummary()
                .flatMap(summary -> {
                    SuccessResponse<ApprovedLoanGlobalSummary> successResponse = SuccessResponse.<ApprovedLoanGlobalSummary>builder()
                            .timestamp(LocalDateTime.now())
                            .path(serverRequest.path())
                            .data(summary)
                            .message("Stats returned successfully")
                            .build();
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(successResponse);
                });
    }
}
