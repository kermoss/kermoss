package io.kermoss.cmd.infra;

import org.springframework.kafka.support.Acknowledgment;

import io.kermoss.cmd.domain.TransporterCommand;

public interface KermossMessageListener {
	
	void commandListener(final TransporterCommand command, Acknowledgment ack);
}
