package io.kermoss.cmd.infra.transporter.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

public final class KafkaCommandTransporterStrategy implements CommandTransporterStrategy {
	private final Logger log = LoggerFactory.getLogger(KafkaCommandTransporterStrategy.class);
	private final KafkaTemplate<String, TransporterCommand> kafkaTemplate;
	private KermossProperties kermossProperties;

	public KafkaCommandTransporterStrategy(KafkaTemplate<String, TransporterCommand> kafkaTemplate,
			KermossProperties kermossProperties) {

		this.kafkaTemplate = kafkaTemplate;
		this.kermossProperties = kermossProperties;
	}

	@Override
	public Boolean transportPrepareCommand(final TransporterCommand command) {
		try {
			return postCommand(command);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	private Boolean postCommand(final TransporterCommand command) {
		String calculateDestination = calculateDestination(command.getDestination());
		try {
			ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send(calculateDestination, command);
			//TODO see opor to make send with get(timeout, unit) 
			send.get();
		    return true;
		} catch (Exception e) {
			log.error("Kermoss in Kafka transport fail to send command", e);	
		   return false;  
		}
	}

	@Override
	public Boolean transportCommand(TransporterCommand command) {
		return postCommand(command);
	}

	private String calculateDestination(final String destinationKey) {
		return  kermossProperties.getKafkaDestination(destinationKey);
	}
}
