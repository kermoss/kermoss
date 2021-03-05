package io.kermoss.bfm.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Component
public class GlobalTrxBFMValidator extends TransactionBFMValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalTrxBFMValidator.class);

	@Autowired(required = false)
	private List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers;

	@Override
	public void validate(List<TrxBoundary> trxBoundaries) {
		if (gtxWorkers != null)
			gtxWorkers.forEach(w -> {
				GlobalTransactionWorker globalTransactionWorker = null;

				try {
					globalTransactionWorker = (GlobalTransactionWorker) ClassUtils.getUserClass(w.getClass())
							.newInstance();
					validateMeta(trxBoundaries, globalTransactionWorker);

					for (Method m : globalTransactionWorker.getClass().getMethods()) {
						boolean intrestingMethod = false;
						if (m.getName().equals("onStart")) {
							intrestingMethod = true;

							if (!isAnnotationPresent(m, BusinessGlobalTransactional.class)) {
								throw new BusinessFlowManagerValidatorException(
										"@BusinessGlobalTransactional annotation must be present on method onStart of "
												+ globalTransactionWorker.getClass().getName());
							}
						}
						if (m.getName().equals("onComplete")) {
							intrestingMethod = true;
							if (!isAnnotationPresent(m, CommitBusinessGlobalTransactional.class)) {
								throw new BusinessFlowManagerValidatorException(
										"@CommitBusinessGlobalTransactional annotation must be present on method onComplete of "
												+ globalTransactionWorker.getClass().getName());
							}
						}

						if (intrestingMethod) {
							validateTrxStepDefinition(globalTransactionWorker, m);
						}
					}

				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | InstantiationException e) {
					LOGGER.error("", e);
				}
			});
	}

	void validateTrxStepDefinition(GlobalTransactionWorker globalTransactionWorker, Method m)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException {

		if (isOnlyPublic(m.getModifiers())) {
			Class p = m.getParameterTypes()[0];
			@SuppressWarnings("rawtypes")
			GlobalTransactionStepDefinition invoke = (GlobalTransactionStepDefinition) m.invoke(globalTransactionWorker,
					p.getConstructor().newInstance());
			if (invoke.getIn() == null) {
				throw new BusinessFlowManagerValidatorException(
						"the In property of GlobalTransactionStepDefinition must be specified for method " + m.getName()
								+ " of globalTransactionWorker " + globalTransactionWorker.getClass().getName());
			}
			if (invoke.getMeta() == null) {
				throw new BusinessFlowManagerValidatorException(
						"the Meta property of GlobalTransactionStepDefinition must be specified for method "
								+ m.getName() + " of globalTransactionWorker "
								+ globalTransactionWorker.getClass().getName());
			}

		}
	}

	void validateMeta(List<TrxBoundary> trxBoundaries, GlobalTransactionWorker globalTransactionWorker) {
		Class<? extends GlobalTransactionWorker> clazz = globalTransactionWorker.getClass();
		if (globalTransactionWorker.getMeta() == null) {
			throw new BusinessFlowManagerValidatorException("Meta Worker is null for globalTransactionWorker " + clazz);
		}
		if (globalTransactionWorker.getMeta().getTransactionName() == null) {
			throw new BusinessFlowManagerValidatorException(
					"Meta Worker transactionName is null for globalTransactionWorker " + clazz);
		}

		TrxBoundary trxBoundary = new TrxBoundary("GlobalTrx", true,
				globalTransactionWorker.getMeta().getTransactionName(), globalTransactionWorker.getMeta().getChildOf(),
				clazz);
		addTrxBoundary(trxBoundaries, trxBoundary);

	}

	public void setGtxWorkers(
			List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers) {
		this.gtxWorkers = gtxWorkers;
	}

	boolean isOnlyPublic(Integer m) {
		int length = String.valueOf(m).length();

		return length != 4 ? true : false;
	}

}