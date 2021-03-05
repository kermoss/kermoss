package io.kermoss.bfm.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
public class LocalTrxBFMValidator extends TransactionBFMValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalTrxBFMValidator.class);
	@Autowired(required = false)
	private List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers;		
	
	

	

	public void validate(List<TrxBoundary> trxBoundaries) {
		if(ltxWorkers!=null)
		ltxWorkers.forEach(w -> {
			LocalTransactionWorker localTransactionWorker = null;

			try {
				localTransactionWorker = (LocalTransactionWorker)  ClassUtils.getUserClass(w.getClass()).newInstance();
				validateMeta(trxBoundaries,localTransactionWorker);

				for (Method m : localTransactionWorker.getClass().getMethods()) {
					boolean intrestingMethod = false;
					if (m.getName().equals("onStart")) {
						intrestingMethod = true;
						if (!isAnnotationPresent(m,BusinessLocalTransactional.class)) {
							throw new BusinessFlowManagerValidatorException(
									"@BusinessLocalTransactional annotation must be present on method onStart of "
											+ localTransactionWorker.getClass());
						}
					}
					if (m.getName().equals("onNext")) {
						intrestingMethod = true;
						if (!isAnnotationPresent(m,SwitchBusinessLocalTransactional.class)) {
							throw new BusinessFlowManagerValidatorException(
									"@SwitchBusinessLocalTransactional annotation must be present on method onNext of "
											+ localTransactionWorker.getClass());
						}
					}
					if (m.getName().equals("onError")) {
						intrestingMethod = true;
						if (!isAnnotationPresent(m,RollBackBusinessLocalTransactional.class)) {
							throw new BusinessFlowManagerValidatorException(
									"@RollBackBusinessLocalTransactional annotation must be present on method onError of "
											+ localTransactionWorker.getClass());
						}
					}
					if (intrestingMethod) {
						validateTrxStepDefinition(localTransactionWorker, m);
					}
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException | InstantiationException  e) {
				LOGGER.error("", e);
			}
		});
	}

	 void validateTrxStepDefinition(LocalTransactionWorker localTransactionWorker, Method m)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException,
			NoSuchMethodException, SecurityException {
		if (isOnlyPublic(m.getModifiers())) {
			Class p = m.getParameterTypes()[0];
			@SuppressWarnings("rawtypes")
			LocalTransactionStepDefinition invoke = (LocalTransactionStepDefinition) m.invoke(localTransactionWorker,
					p.getConstructor().newInstance());
			if (invoke.getIn() == null) {
				throw new BusinessFlowManagerValidatorException(
						"the In property of LocalTransactionStepDefinition must be specified for method " + m.getName()
								+ " of localTransactionWorker " + localTransactionWorker.getClass());
			}
			if (invoke.getMeta() == null) {
				throw new BusinessFlowManagerValidatorException(
						"the Meta property of LocalTransactionStepDefinition must be specified for method "
								+ m.getName() + " of localTransactionWorker " + localTransactionWorker.getClass());
			}

		}
	}

	 void validateMeta(List<TrxBoundary> trxBoundaries,LocalTransactionWorker localTransactionWorker) {
		Class<? extends LocalTransactionWorker> clazz = localTransactionWorker.getClass();
		if (localTransactionWorker.getMeta() == null) {
			throw new BusinessFlowManagerValidatorException("Meta Worker is null for localTransactionWorker " + clazz.getName());
		}
		if (localTransactionWorker.getMeta().getTransactionName() == null) {
			throw new BusinessFlowManagerValidatorException(
					"Meta Worker transactionName is null for localTransactionWorker " + clazz.getName());
		}
		if (localTransactionWorker.getMeta().getChildOf() == null) {
			throw new BusinessFlowManagerValidatorException(
					"Meta Worker childOf is null for localTransactionWorker " + clazz.getName());
		}

		TrxBoundary trxBoundary = new TrxBoundary("LocalTrx", false,
				localTransactionWorker.getMeta().getTransactionName(), localTransactionWorker.getMeta().getChildOf(),
				clazz);
		addTrxBoundary(trxBoundaries,trxBoundary);
	}
	 
	 boolean isOnlyPublic(Integer m){
			int length = String.valueOf(m).length();
			return length!=4?true:false;
	}
	 
	 public void setLtxWorkers(
				List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers) {
			this.ltxWorkers = ltxWorkers;
		}
	
}