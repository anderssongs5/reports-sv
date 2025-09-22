package co.com.powerup.ags.reports.api;

import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, RouterRestTest.TestSecurityConfig.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private ApprovedLoanReportUseCase approvedLoanReportUseCase;

    @Test
    void shouldReturnApprovedLoanSummaryWhenDataExists() {
        ApprovedLoanGlobalSummary mockSummary = new ApprovedLoanGlobalSummary(10L, new BigDecimal("50000.00"));
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(mockSummary));

        webTestClient.get()
                .uri("/api/v1/reports")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(10)
                .jsonPath("$.data.totalAmount").isEqualTo(50000.00)
                .jsonPath("$.message").isEqualTo("Stats returned successfully");
    }

    @Test
    void shouldReturnNoContentWhenNoDataExists() {
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/reports")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnInternalServerErrorWhenUseCaseFails() {
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        webTestClient.get()
                .uri("/api/v1/reports")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldReturnNotFoundWhenUsingPOSTMethod() {
        webTestClient.post()
                .uri("/api/v1/reports")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnNotFoundWhenAccessingNonExistentEndpoint() {
        webTestClient.get()
                .uri("/api/v1/non-existent")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldHandleZeroValuesInResponse() {
        ApprovedLoanGlobalSummary zeroSummary = new ApprovedLoanGlobalSummary(0L, BigDecimal.ZERO);
        when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(zeroSummary));

        webTestClient.get()
                .uri("/api/v1/reports")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.totalCount").isEqualTo(0)
                .jsonPath("$.data.totalAmount").isEqualTo(0);
    }

    @Configuration
    @EnableWebFluxSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .build();
        }
    }
}
