package com.example.helloworld.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DB2_HOST:localhost}")
    private String db2Host;

    @Value("${DB2_PORT:50000}")
    private int db2Port;

    @Value("${DB2_DATABASE:hms}")
    private String db2Database;

    @Value("${DB2_USERNAME:appdata}")
    private String db2Username;

    @Value("${DB2_PASSWORD:app}")
    private String db2Password;

    @Value("${pulsesquare.db2.fallback-enabled:true}")
    private boolean fallbackEnabled;

    private boolean usingFallback = false;

    @Bean
    @Primary
    public DataSource dataSource() {


        boolean db2Reachable = isDb2Reachable(db2Host, db2Port, 1200);

        if (db2Reachable) {
            usingFallback = false;

            return createDb2DataSource();
        } else if (!fallbackEnabled) {
            usingFallback = false;

            return createDb2DataSource();
        } else {
            usingFallback = true;

            return createFallbackDataSource();
        }
    }

    public boolean isUsingFallback() {
        return usingFallback;
    }

    public String getDb2Host() {
        return db2Host;
    }

    public int getDb2Port() {
        return db2Port;
    }

    public String getDb2Database() {
        return db2Database;
    }

    public String getDb2Username() {
        return db2Username;
    }

    private boolean isDb2Reachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private DataSource createDb2DataSource() {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = String.format("jdbc:db2://%s:%d/%s", db2Host, db2Port, db2Database);
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        config.setUsername(db2Username);
        config.setPassword(db2Password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(15000);
        config.setPoolName("PulseSquare-IBM-Db2-Pool");
        return new HikariDataSource(config);
    }

    private DataSource createFallbackDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:hms;MODE=DB2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setPoolName("PulseSquare-Embedded-DB2-Simulation-Pool");
        return new HikariDataSource(config);
    }


}
