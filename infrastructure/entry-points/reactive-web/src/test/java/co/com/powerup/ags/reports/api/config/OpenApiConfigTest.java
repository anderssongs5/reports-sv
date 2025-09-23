package co.com.powerup.ags.reports.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    void shouldCreateOpenAPIWithCorrectConfiguration() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result).isNotNull();
        assertThat(result.getInfo()).isNotNull();
        assertThat(result.getInfo().getTitle()).isEqualTo("Loan Reports Service API");
        assertThat(result.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void shouldIncludeContactInformation() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result.getInfo().getContact()).isNotNull();
        assertThat(result.getInfo().getContact().getName()).isEqualTo("Andersson García");
        assertThat(result.getInfo().getContact().getEmail()).isEqualTo("andersson.garcia@powerup.com");
    }

    @Test
    void shouldIncludeServerConfiguration() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "9090");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result.getServers()).isNotEmpty();
        assertThat(result.getServers().get(0).getUrl()).isEqualTo("http://localhost:9090");
        assertThat(result.getServers().get(0).getDescription()).isEqualTo("Development server");
    }

    @Test
    void shouldIncludeSecurityConfiguration() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result.getSecurity()).isNotEmpty();
        assertThat(result.getComponents()).isNotNull();
        assertThat(result.getComponents().getSecuritySchemes()).containsKey("Bearer Authentication");
    }

    @Test
    void shouldIncludeTagsConfiguration() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result.getTags()).isNotEmpty();
        assertThat(result.getTags().get(0).getName()).isEqualTo("Loan Reports");
        assertThat(result.getTags().get(0).getDescription()).isEqualTo("Operations for retrieving approved loan statistics and reports");
    }

    @Test
    void shouldHandleNullServerPort() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", null);

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result).isNotNull();
        assertThat(result.getServers().get(0).getUrl()).isEqualTo("http://localhost:null");
    }

    @Test
    void shouldIncludeApiDescription() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        assertThat(result.getInfo().getDescription()).contains("API for managing approved loan reports and statistics");
        assertThat(result.getInfo().getDescription()).contains("This service provides endpoints to retrieve global summaries");
    }

    @Test
    void shouldConfigureJWTSecurityScheme() {
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");

        OpenAPI result = openApiConfig.customOpenAPI();

        var securityScheme = result.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertThat(securityScheme.getType()).isEqualTo(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }
}