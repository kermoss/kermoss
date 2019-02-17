package io.kermoss.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.support.RetryTemplate;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Autowired
	private KermossProperties kermossProperties;

	public ConsumerFactory<String,TransporterCommand> consumerFactory() {
		String bootstrapAddress = kermossProperties.getTransport().getKafka().getBootstrapAddress();
		String groupName = kermossProperties.getTransport().getKafka().getConsumer().getGroupName();
		if(groupName==null) {
			throw new IllegalArgumentException("Kermoss: Kafka groupName property is not specified");
			
		}
		if(bootstrapAddress==null) {
			throw new IllegalArgumentException("Kermoss: Kafka bootstrapAddress property is not specified");
		}
		
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TransporterCommand.class));
    }
    
	@Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name="kermoss.transport.default-layer",havingValue="kafka")
    public ConcurrentKafkaListenerContainerFactory<String, TransporterCommand> kafkaManualAckListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransporterCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        ContainerProperties props = factory.getContainerProperties();
        props.setAckMode(AckMode.MANUAL_IMMEDIATE);
        RetryTemplate retryTemplate = new RetryTemplate();
        //TODO configure retry & recovery callback
        factory.setRetryTemplate(retryTemplate);
		factory.setRecoveryCallback(c -> null);
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}