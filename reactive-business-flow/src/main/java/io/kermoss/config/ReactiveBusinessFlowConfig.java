package io.kermoss.config;

import feign.Client;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.FeignCommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.RestCommandTransporterStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = {"io.kermoss.cmd.domain.repository", "io.kermoss.trx.domain.repository", "io.kermoss.trx.domain.repository"},
//    entityManagerFactoryRef = "kermossemf"
//)
//@EntityScan({"io.kermoss.trx.domain", "io.kermoss.domain", "io.kermoss.cmd.domain"})
public class ReactiveBusinessFlowConfig {
//    @Autowired(required = false)
//    PersistenceUnitManager persistenceUnitManager;
//
//    @Bean(name="kermossemfb")
//    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
//        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//        adapter.setShowSql(jpaProperties.isShowSql());
//        adapter.setDatabase(jpaProperties.getDatabase());
//        adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
//        adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
//        return new EntityManagerFactoryBuilder(
//            adapter,
//            jpaProperties.getProperties(),
//            this.persistenceUnitManager
//        );
//    }
//
//    @Bean(name="kermossemf")
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//        @Qualifier("kermossemfb") EntityManagerFactoryBuilder factoryBuilder,
//        DataSource dataSource
//    ) {
//        return factoryBuilder
//            .dataSource(dataSource)
//            .packages("io.kermoss.trx.domain", "io.kermoss.domain", "io.kermoss.cmd.domain")
//            .build();
//    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @Conditional(ReactiveBusinessFlowConfig.MissingTransporterStrategyBean.class)
    public CommandTransporterStrategy strategy(final RestTemplate restTemplate, final Environment environment, final Client client) {
        if("rest".equals(environment.getProperty("kermoss.transport.strategy"))) {
            return new RestCommandTransporterStrategy(restTemplate, environment);
        } else {
            return new FeignCommandTransporterStrategy(client, FeignCommandTransporterStrategy::defaultClientFactory);
        }
    }

    public static class MissingTransporterStrategyBean implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return context.getBeanFactory().getBeansOfType(CommandTransporterStrategy.class).isEmpty();
        }
    }
}
