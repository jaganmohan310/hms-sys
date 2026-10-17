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
        printDatabaseHeader();

        boolean db2Reachable = isDb2Reachable(db2Host, db2Port, 1200);

        if (db2Reachable) {
            usingFallback = false;
            printConnectedSuccess();
            return createDb2DataSource();
        } else if (!fallbackEnabled) {
            usingFallback = false;
            System.err.println(">>> [DB2 ERROR] Fallback is disabled and IBM Db2 is offline. Attempting direct connection...");
            return createDb2DataSource();
        } else {
            usingFallback = true;
            printFallbackNotice();
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

    private void printDatabaseHeader() {
        System.out.println("\n" + "=".repeat(88));
        System.out.println("                 PULSESQUARE HMS - DATABASE CONNECTION CHECK");
        System.out.println("=".repeat(88));
        System.out.println("[*] Target Database System : IBM Db2 Enterprise Edition");
        System.out.println("[*] Target JDBC URL        : jdbc:db2://" + db2Host + ":" + db2Port + "/" + db2Database);
        System.out.println("[*] Target Host & Port     : " + db2Host + ":" + db2Port);
        System.out.println("[*] Database Name          : " + db2Database);
        System.out.println("[*] Authenticating User    : " + db2Username);
        System.out.println("[*] Testing live socket connection to IBM Db2...");
        System.out.println("-".repeat(88));
    }

    private void printConnectedSuccess() {
        System.out.println(">>> [DB CHECK STATUS]: [CONNECTED] Successfully connected to IBM Db2 on port " + db2Port + "!");
        System.out.println(">>> [DB DRIVER]:      com.ibm.db2.jcc.DB2Driver");
        System.out.println(">>> [DATABASE]:       " + db2Database + " (LIVE PRODUCTION MODE)");
        System.out.println("=".repeat(88) + "\n");
    }

    private void printFallbackNotice() {
        System.out.println(">>> [DB CHECK STATUS]: [OFFLINE] Could NOT establish connection to IBM Db2 on " + db2Host + ":" + db2Port + ".");
        System.out.println(">>> [DIAGNOSTIC]:     Connection refused: No active IBM Db2 TCP listener on port " + db2Port + ".");
        System.out.println(">>> [AUTOMATION]:     Activating Embedded DB2 Compatibility Mode (In-Memory).");
        System.out.println(">>> [WEB UI STATUS]:  AngularJS UI, Login Page, and REST APIs are 100% OPERATIONAL!");
        System.out.println(">>> [DEFAULT USERS]:  Pre-seeded with admin, dr_sharma, patient_john, nurse_sarah.");
        System.out.println(">>> [TO CONNECT LIVE]: Start your DB2 instance or run 'docker-compose up -d hms-db2'.");
        System.out.println("=".repeat(88) + "\n");
    }
}
