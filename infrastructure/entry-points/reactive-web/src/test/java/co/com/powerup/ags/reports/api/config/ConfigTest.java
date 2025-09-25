package co.com.powerup.ags.reports.api.config;

import co.com.powerup.ags.reports.api.Handler;
import co.com.powerup.ags.reports.api.RouterRest;
import co.com.powerup.ags.reports.usecase.approvedloanreport.ApprovedLoanReportUseCase;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanSummary;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest(excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration.class
})
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private ApprovedLoanReportUseCase approvedLoanReportUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        Mockito.when(approvedLoanReportUseCase.getApprovedLoanGlobalSummary())
                .thenReturn(Mono.just(new ApprovedLoanSummary(1L, BigDecimal.TWO)));
        
        webTestClient.get()
                .uri("/api/v1/reports")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}