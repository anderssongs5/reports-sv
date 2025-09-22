package co.com.powerup.ags.reports.api;

import co.com.powerup.ags.reports.api.dto.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@Tag(name = "Loan Reports", description = "API endpoints for approved loan reports and statistics")
public class RouterRest {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/reports",
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getApprovedGlobalSummary",
                summary = "Get approved loans global summary",
                description = "Retrieves global statistics for all approved loans including total count and total amount",
                tags = {"Loan Reports"},
                security = @SecurityRequirement(name = "Bearer Authentication"),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved approved loans global summary",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Success Response",
                                    value = """
                                                {
                                                     "timestamp": "2025-09-21T22:04:09.721837",
                                                     "path": "/api/v1/reports",
                                                     "data": {
                                                         "totalCount": 7,
                                                         "totalAmount": 1200
                                                     },
                                                     "message": "Stats returned successfully"
                                                 }
                                                """
                                        )
                        )
                    ),
                    @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized - Invalid or missing authentication token"
                    ),
                    @ApiResponse(
                        responseCode = "500", 
                        description = "Internal server error occurred while processing the request"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return RouterFunctions.route()
                .path("/api/v1/reports", builder -> builder
                        .GET("", accept(MediaType.APPLICATION_JSON), handler::getApprovedGlobalSummary))
                .build();
    }
}
