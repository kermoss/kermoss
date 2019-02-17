package io.kermoss.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

@Configuration
public class KafkaProducerConfig {

	@Autowired
	private KermossProperties kermossProperties;

    
	
    public ProducerFactory<String, TransporterCommand> producerFactory() {
    	String bootstrapAddress = kermossProperties.getTransport().getKafka().getBootstrapAddress();
		
		if(bootstrapAddress==null) {
			throw new IllegalArgumentException("Kermoss: Kafka bootstrapAddress property is not specified");
		}
    	Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name="kermoss.transport.default-layer",havingValue="kafka")
    public KafkaTemplate<String, TransporterCommand> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
}