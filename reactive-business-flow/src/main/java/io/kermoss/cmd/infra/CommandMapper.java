package io.kermoss.cmd.infra;

import org.springframework.stereotype.Component;

import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;

@Component
public class CommandMapper {

	public InboundCommand transform(TransporterCommand outcmd) {

		return InboundCommand.builder().source(outcmd.getSource()).subject(outcmd.getSubject())
				.destination(outcmd.getDestination()).payload(outcmd.getPayload()).PGTX(outcmd.getParentGTX())
				.gTX(outcmd.getChildofGTX()).fLTX(outcmd.getFLTX()).additionalHeaders(outcmd.getAdditionalHeaders())
				.refId(outcmd.getRefId()).trace(outcmd.getTraceId()).build();
	}
}
