package com.pulsesquare.hms.ws.controller;

import com.pulsesquare.hms.config.DataSourceConfig;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enterprise System Health & Observability Controller.
 * Provides direct, lightweight health and operational telemetry probes
 * without routing through Swagger UI.
 */
@Hidden
@RestController
public class HealthController {

    private final DataSourceConfig dataSourceConfig;
    private final DataSource dataSource;
    private final long startTimeMillis = System.currentTimeMillis();

    @Autowired
    public HealthController(DataSourceConfig dataSourceConfig, DataSource dataSource) {
        this.dataSourceConfig = dataSourceConfig;
        this.dataSource = dataSource;
    }

    @GetMapping(value = {"/health", "/api/v1/health"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", "PulseSquare-Enterprise-HMS");
        health.put("version", "1.0.0");
        health.put("timestamp", Instant.now().toString());

        // 1. Database Connectivity Probe
        Map<String, Object> dbHealth = new LinkedHashMap<>();
        long dbStart = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1 FROM SYSIBM.SYSDUMMY1");
            dbHealth.put("status", "UP");
            dbHealth.put("responseTimeMs", System.currentTimeMillis() - dbStart);
        } catch (Exception ex) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", ex.getMessage());
            health.put("status", "DEGRADED");
        }
        dbHealth.put("engine", "IBM Db2 Enterprise");
        dbHealth.put("host", dataSourceConfig.getDb2Host());
        dbHealth.put("port", dataSourceConfig.getDb2Port());
        dbHealth.put("database", dataSourceConfig.getDb2Database());

        // 2. JVM & Runtime Telemetry
        Map<String, Object> runtimeHealth = new LinkedHashMap<>();
        runtimeHealth.put("status", "UP");
        runtimeHealth.put("javaVersion", System.getProperty("java.version"));
        runtimeHealth.put("jvmVendor", System.getProperty("java.vm.vendor"));
        runtimeHealth.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        runtimeHealth.put("uptimeSeconds", (System.currentTimeMillis() - startTimeMillis) / 1000);

        // 3. Memory Metrics
        Map<String, Object> memoryHealth = new LinkedHashMap<>();
        long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long maxMem = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        memoryHealth.put("usedMemoryMb", totalMem - freeMem);
        memoryHealth.put("freeMemoryMb", freeMem);
        memoryHealth.put("totalMemoryMb", totalMem);
        memoryHealth.put("maxMemoryMb", maxMem);

        Map<String, Object> components = new LinkedHashMap<>();
        components.put("database", dbHealth);
        components.put("runtime", runtimeHealth);
        components.put("memory", memoryHealth);

        health.put("components", components);

        return ResponseEntity.ok(health);
    }
}
