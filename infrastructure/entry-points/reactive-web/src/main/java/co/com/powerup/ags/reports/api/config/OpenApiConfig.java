package co.com.powerup.ags.reports.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("maria.gonzalez@powerup.com");
        contact.setName("María González");

        return new OpenAPI()
                .info(new Info()
                        .title("Loan Reports Service API")
                        .description("API for managing approved loan reports and statistics. " +
                                   "This service provides endpoints to retrieve global summaries " +
                                   "and statistics for approved loans in the system.")
                        .version("1.0.0")
                        .contact(contact))
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token required for authentication")
                        )
                )
                .tags(List.of(
                        new Tag()
                                .name("Loan Reports")
                                .description("Operations for retrieving approved loan statistics and reports")
                ));
    }
}