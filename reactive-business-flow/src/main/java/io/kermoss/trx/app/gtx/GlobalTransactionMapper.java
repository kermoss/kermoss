package io.kermoss.trx.app.gtx;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;

@Component
@Transactional(readOnly = true)
public class GlobalTransactionMapper {
	@Autowired
	private BubbleCache bubbleCache;
	@Autowired
	private CommandRepository commandRepository;
	@Autowired
	private TransactionUtilities txUtility;

	Optional<RequestGlobalTransaction> mapTo(GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> pipeline) {
		Optional<BubbleMessage> bubleMessage = txUtility.getBubleMessage(pipeline);

		RequestGlobalTransaction.RequestGlobalTransactionBuilder requestGlobalTransactionBuilder = new RequestGlobalTransaction.RequestGlobalTransactionBuilder();
		requestGlobalTransactionBuilder.name(pipeline.getMeta().getTransactionName());
		requestGlobalTransactionBuilder.eventRequestor(pipeline.getIn());

		bubleMessage.ifPresent(bm -> {
			requestGlobalTransactionBuilder.gtx(bm.getGLTX()).parent(bm.getPGTX()).traceId(bm.getTraceId());
			if (bm.getCommandId() != null) {
				InboundCommand inboundCommand = (InboundCommand) commandRepository.findOne(bm.getCommandId());
				requestGlobalTransactionBuilder.commandRequestor(inboundCommand);
			}
		});

		RequestGlobalTransaction rgt = requestGlobalTransactionBuilder.build();
		return Optional.of(rgt);

	}
}