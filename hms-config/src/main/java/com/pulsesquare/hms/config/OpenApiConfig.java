package com.pulsesquare.hms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Enterprise OpenAPI 3.0 Documentation Metadata Configuration.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PulseSquare Healthcare Management System (HMS) REST API")
                        .version("1.0.0")
                        .description("Enterprise RESTful API for Patient Care, Medical Staff Administration, "
                                + "Clinical Procedures, and JWT-authenticated access powered by jOOQ and IBM Db2.")
                        .contact(new Contact()
                                .name("PulseSquare HMS Engineering Team")
                                .email("architecture@pulsesquare.com")
                                .url("https://pulsesquare.com/hms/docs"))
                        .license(new License()
                                .name("Healthcare Enterprise Platform License")
                                .url("https://pulsesquare.com/license")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Active HMS Server Instance")));
    }
}
