package io.kermoss.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import feign.Client;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.FeignCommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.KafkaCommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.RestCommandTransporterStrategy;
import io.kermoss.props.KermossProperties;
import io.kermoss.props.Layer;

@Configuration
@EnableFeignClients
public class ReactiveBusinessFlowConfig {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(final RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	@Conditional(ReactiveBusinessFlowConfig.MissingTransporterStrategyBean.class)
	public CommandTransporterStrategy strategy(KafkaTemplate<String, TransporterCommand> kafkaTemplate,
			RestTemplate restTemplate, final KermossProperties kermossProperties, final Client client) {
		
		Layer layer = kermossProperties.getTransport().getDefaultLayer();
		
		CommandTransporterStrategy commandTransporterStrategy;

		switch (layer) {
		case HTTP:
			commandTransporterStrategy = new RestCommandTransporterStrategy(restTemplate, kermossProperties);
			break;
		case FEIGN:
			commandTransporterStrategy = new FeignCommandTransporterStrategy(client,kermossProperties,
					FeignCommandTransporterStrategy::defaultClientFactory);
			break;
		default:
			commandTransporterStrategy = new KafkaCommandTransporterStrategy(kafkaTemplate, kermossProperties);
			break;
		}

		return commandTransporterStrategy;
	}

	public static class MissingTransporterStrategyBean implements Condition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getBeanFactory().getBeansOfType(CommandTransporterStrategy.class).isEmpty();
		}
	}
}
