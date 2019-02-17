package io.kermoss.cmd.infra;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;

@Component
@ConditionalOnProperty(name="kermoss.transport.default-layer",havingValue="kafka")
public class KafkaCommandListener {
    
	private final Logger LOG = LoggerFactory.getLogger(KafkaCommandListener.class);
	@Autowired
	private CommandOrchestrator commandOrchestrator;
	
	private CountDownLatch latch = new CountDownLatch(1);
    
	@KafkaListener(topics= "#{kermossProperties.topics()}", containerFactory = "kafkaManualAckListenerContainerFactory")
	public void commandListener(final TransporterCommand command, Acknowledgment ack) {
		if(!commandOrchestrator.isInboundCommandExist(command.getRefId())){
			this.commandOrchestrator.receive(this.transform(command));
        }
		if(LOG.isDebugEnabled()) {
			LOG.debug("Recieved Transport Command message: {}", command);
		}
		ack.acknowledge();
		this.latch.countDown();
	}

	//TODO to be refactored
	private InboundCommand transform(TransporterCommand outcmd) {

		return InboundCommand.builder().source(outcmd.getSource()).subject(outcmd.getSubject())
				.destination(outcmd.getDestination()).payload(outcmd.getPayload()).PGTX(outcmd.getParentGTX())
				.gTX(outcmd.getChildofGTX()).fLTX(outcmd.getFLTX()).additionalHeaders(outcmd.getAdditionalHeaders())
				.refId(outcmd.getRefId()).trace(outcmd.getTraceId()).build();
	}
}
