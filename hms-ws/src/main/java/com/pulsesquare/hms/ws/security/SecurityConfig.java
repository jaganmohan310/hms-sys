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
                // Static Assets & AngularJS Frontend
                .antMatchers("/", "/index.html", "/login.html", "/dashboard.html",
                             "/static/**", "/images/**", "/js/**", "/webjars/**", "/favicon.ico")
                    .permitAll()
                // OpenAPI / Swagger Documentation
                .antMatchers("/hms", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/openapi/**")
                    .permitAll()
                // Public Authentication Endpoints (supports both /api/v1/auth and legacy /api/auth)
                .antMatchers(HttpMethod.POST,
                        "/api/v1/auth/login", "/api/v1/auth/signup", "/api/v1/auth/logout",
                        "/api/auth/login", "/api/auth/register", "/api/auth/signup", "/api/auth/logout")
                    .permitAll()
                // Health & Diagnostics
                .antMatchers(HttpMethod.GET,
                        "/api/v1/auth/status", "/api/auth/status", "/actuator/**")
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
