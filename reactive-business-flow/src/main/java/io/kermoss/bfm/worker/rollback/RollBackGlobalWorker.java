package io.kermoss.bfm.worker.rollback;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.cmd.RollBackGlobalCommand;
import io.kermoss.bfm.decoder.RollBackGlobalDecoder;
import io.kermoss.bfm.event.ErrorGlobalOccured;
import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.event.GlobalRollbackOccured;
import io.kermoss.bfm.event.GlobalTransactionLevelRollbacked;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.cmd.infra.translator.LanguageTranslator;
import io.kermoss.props.KermossProperties;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
public class RollBackGlobalWorker
		extends LocalTransactionWorker<GlobalRollbackOccured, GlobalTransactionLevelRollbacked, ErrorGlobalOccured> {
	@Autowired
	private KermossProperties kermossProperties;
	@Autowired
	private LanguageTranslator languageTranslator;

	public RollBackGlobalWorker() {
		super(new WorkerMeta("RollBackGlobalService","anyLocalTrx"));
	}

	@PostConstruct
	public void init() {
		languageTranslator.registerDecoder("rollback-global", new RollBackGlobalDecoder());
	}

	@Override
	@BusinessLocalTransactional
	public LocalTransactionStepDefinition onStart(GlobalRollbackOccured globalRollbackOccured) {
		WorkerMeta workerMeta = new WorkerMeta(this.meta.getTransactionName(), globalRollbackOccured.getTrxChildOf());
		Stream<BaseTransactionCommand> sourceStream = null;
		if (kermossProperties != null) {
			sourceStream = kermossProperties.getSources().keySet().stream()
					.map(c -> new RollBackGlobalCommand("rollback-global", null, null, c));
		}
		return LocalTransactionStepDefinition.builder().in(globalRollbackOccured).send(sourceStream)
				.blow(Stream.of(new ErrorLocalOccured(),
						new GlobalTransactionLevelRollbacked(globalRollbackOccured.getTrxChildOf())))
				.meta(workerMeta).build();

	}

	@Override
	@SwitchBusinessLocalTransactional
	public LocalTransactionStepDefinition onNext(GlobalTransactionLevelRollbacked globalTransactionLevelRollbacked) {
		WorkerMeta workerMeta = new WorkerMeta(this.meta.getTransactionName(),
				globalTransactionLevelRollbacked.getTrxChildOf());
		return LocalTransactionStepDefinition.builder().in(globalTransactionLevelRollbacked).meta(workerMeta).build();
	}

	@Override
	@RollBackBusinessLocalTransactional
	public LocalTransactionStepDefinition onError(ErrorGlobalOccured errorGlobalOccured) {
		return LocalTransactionStepDefinition.builder().in(errorGlobalOccured).meta(this.meta).build();
	}
}
