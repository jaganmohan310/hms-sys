package com.pulsesquare.hms.ft;

import com.pulsesquare.hms.config.DataSourceConfig;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@SpringBootApplication(
        scanBasePackages = "com.pulsesquare.hms",
        exclude = {
                FlywayAutoConfiguration.class,
                DataSourceAutoConfiguration.class
        }
)
public class HmsFunctionalTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmsFunctionalTestApplication.class, args);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public DataSourceConfig dataSourceConfig() {
        DataSourceConfig config = Mockito.mock(DataSourceConfig.class);
        Mockito.when(config.getDb2Host()).thenReturn("localhost");
        Mockito.when(config.getDb2Port()).thenReturn(50000);
        Mockito.when(config.getDb2Database()).thenReturn("hms");
        return config;
    }
}
