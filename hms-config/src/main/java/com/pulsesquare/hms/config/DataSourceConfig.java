package com.pulsesquare.hms.config;

import com.pulsesquare.hms.db.FlywayMigrationService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Enterprise Production DataSource Configuration for real IBM Db2 Database.
 * Powered by HikariCP Connection Pool and Flyway Migration Integration.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DB2_HOST:localhost}")
    private String db2Host;

    @Value("${DB2_PORT:50000}")
    private int db2Port;

    @Value("${DB2_DATABASE:hms}")
    private String db2Database;

    @Value("${DB2_USERNAME:db2inst1}")
    private String db2Username;

    @Value("${DB2_PASSWORD:app}")
    private String db2Password;

    @Autowired(required = false)
    private FlywayMigrationService flywayMigrationService;

    @Bean
    @Primary
    public DataSource dataSource() {
        String jdbcUrl = String.format("jdbc:db2://%s:%d/%s", db2Host, db2Port, db2Database);
        logger.info("Initializing Enterprise IBM Db2 Connection Pool for URL: {}", jdbcUrl);

        // Pre-flight check to verify if Db2 is listening
        if (!isDb2Listening(db2Host, db2Port, 1500)) {
            logger.error("\n" +
                    "========================================================================================\n" +
                    "[CRITICAL ERROR] IBM Db2 is NOT running or listening at {}:{}!\n" +
                    "Connection refused: No active database listener detected on port {}.\n" +
                    "\n" +
                    "HOW TO START YOUR IBM Db2 DATABASE:\n" +
                    "1. Open Command Prompt as Administrator and run:\n" +
                    "       net start DB2\n" +
                    "2. Or open 'Db2 Command Window - Administrator' and run:\n" +
                    "       db2start\n" +
                    "       db2set DB2COMM=TCPIP\n" +
                    "       db2 update dbm cfg using SVCENAME 50000\n" +
                    "3. Ensure the '{}' database exists:\n" +
                    "       db2 create database {} using codeset UTF-8 territory US\n" +
                    "\n" +
                    "If using a remote Db2 instance, configure environment variables:\n" +
                    "       set DB2_HOST=<ip-or-host>\n" +
                    "       set DB2_PORT=50000\n" +
                    "       set DB2_DATABASE={}\n" +
                    "       set DB2_USERNAME=appdata\n" +
                    "       set DB2_PASSWORD=app\n" +
                    "========================================================================================",
                    db2Host, db2Port, db2Port, db2Database, db2Database, db2Database);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
        config.setUsername(db2Username);
        config.setPassword(db2Password);
        config.setPoolName("PulseSquare-IBM-Db2-EnterprisePool");
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(20000);
        config.setConnectionTestQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");

        // Custom Db2 properties
        config.addDataSourceProperty("currentSchema", db2Username.toUpperCase());
        config.addDataSourceProperty("retrieveMessagesFromServerOnGetMessage", "true");

        HikariDataSource ds = new HikariDataSource(config);
        logger.info("IBM Db2 DataSource initialized successfully (Driver: com.ibm.db2.jcc.DB2Driver, Target: {}).", jdbcUrl);

        // Run Flyway database migrations against real IBM Db2
        if (flywayMigrationService != null) {
            try {
                logger.info("Running Flyway schema migrations on real IBM Db2 database...");
                flywayMigrationService.runMigration(ds);
            } catch (Exception e) {
                logger.error("Flyway migration on IBM Db2 encountered an error: {}", e.getMessage(), e);
                throw new IllegalStateException("Failed to run Flyway migrations on IBM Db2: " + e.getMessage(), e);
            }
        }

        return ds;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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

    private boolean isDb2Listening(String host, int port, int timeoutMs) {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
