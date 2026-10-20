package com.pulsesquare.hms.db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(FlywayMigrationService.class);

    public MigrateResult runMigration(DataSource dataSource) {
        logger.info("Executing Flyway database migrations from classpath:db/migration...");
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .outOfOrder(true)
                .load();

        MigrateResult result = flyway.migrate();
        logger.info("Flyway migration completed successfully. Applied {} migrations. Current version: {}",
                result.migrationsExecuted, result.targetSchemaVersion);
        return result;
    }
}
