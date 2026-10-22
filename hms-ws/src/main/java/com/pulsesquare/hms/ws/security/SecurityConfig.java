package com.pulsesquare.hms.ws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage()))
            .and()
            .authorizeRequests()
                // Portal & Swagger Documentation
                .antMatchers("/", "/hms", "/hms/", "/hms/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/openapi/**", "/static/**")
                    .permitAll()
                // Public Health & Diagnostics Endpoints (No Swagger, Direct Access)
                .antMatchers(HttpMethod.GET,
                        "/health", "/health/**", "/api/v1/health", "/api/v1/auth/status", "/api/auth/status", "/actuator/**")
                    .permitAll()
                // Public Authentication Endpoints (supports both /api/v1/auth and legacy /api/auth)
                .antMatchers(HttpMethod.POST,
                        "/api/v1/auth/login", "/api/v1/auth/signup", "/api/v1/auth/logout",
                        "/api/auth/login", "/api/auth/register", "/api/auth/signup", "/api/auth/logout")
                    .permitAll()
                // Protected Endpoints
                .antMatchers("/api/v1/auth/me", "/api/auth/me")
                    .authenticated()
                .anyRequest()
                    .authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
