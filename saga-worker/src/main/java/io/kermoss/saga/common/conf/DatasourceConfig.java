package io.kermoss.saga.common.conf;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    @ConfigurationProperties(prefix="spring.datasource")
    @Bean
    public DataSource myDataSource() {
        DataSource ds =  DataSourceBuilder.create().build();
        return ds;
    }
}
