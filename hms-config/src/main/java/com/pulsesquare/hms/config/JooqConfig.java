package com.pulsesquare.hms.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

/**
 * Enterprise jOOQ DSLContext Configuration for Type-safe SQL querying on IBM Db2.
 */
@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dslContext(DataSource dataSource) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(dataSource);
        DataSourceConnectionProvider connectionProvider = new DataSourceConnectionProvider(proxy);

        Settings settings = new Settings()
                .withRenderNameCase(RenderNameCase.UPPER)
                .withRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED);

        DefaultConfiguration jooqConfig = new DefaultConfiguration();
        jooqConfig.set(connectionProvider);
        jooqConfig.set(settings);
        jooqConfig.set(SQLDialect.DEFAULT);

        return DSL.using(jooqConfig);
    }
}
