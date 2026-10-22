package com.pulsesquare.hms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Enterprise OpenAPI 3.0 Documentation Metadata & Grouped Version Configuration.
 * Segregates core HMS functionality into versioned API groups (v1, v2)
 * and keeps system health & Actuator strictly separate.
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
                                + "Clinical Procedures, and JWT-authenticated access powered by jOOQ and IBM Db2. "
                                + "System Health & Actuator are segregated from functional business specifications.")
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

    /**
     * Group v1: Primary Functional HMS Business Endpoints (Authentication, Patients, Clinical, Billing)
     */
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .displayName("PulseSquare HMS REST API v1.0 (Core Functionality)")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude("/actuator/**", "/health", "/api/v1/health")
                .build();
    }

    /**
     * Group v2: Next-Gen Interoperability Endpoints (HL7 FHIR R4 & Event-Driven)
     */
    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("PulseSquare HMS REST API v2.0 (Next-Gen FHIR & Interoperability)")
                .pathsToMatch("/api/v2/**")
                .pathsToExclude("/actuator/**", "/health")
                .build();
    }
}
