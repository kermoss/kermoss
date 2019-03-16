package io.kermoss.bfm.validator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNBDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNCDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNInDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNMetaDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerWNBDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerWNInDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerWNMetaDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerWNRDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerWNSDummy;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.LocalTransactionWorker;

@RunWith(MockitoJUnitRunner.class)
public class LocalTrxBFMValidatorTest {

	@Spy
	private LocalTrxBFMValidator localTrxBFMValidatorUnderTest;

	@Test
	public void testWithNoProblem() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		localTrxBFMValidatorUnderTest.validate(trxBoundaries);
	}

	@Test
	public void testWithNoBusinessLocalTransactional() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerWNBDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "@BusinessLocalTransactional annotation must be present";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			localTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}
	
	
	@Test
	public void testWithSwitchBusinessLocalTransactional() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerWNSDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "@SwitchBusinessLocalTransactional annotation must be present";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			localTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}
	
	
	@Test
	public void testWithRollBackBusinessLocalTransactional() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerWNRDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "@RollBackBusinessLocalTransactional annotation must be present";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			localTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}
	
	@Test
	public void testWithNoInProperty() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerWNInDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "the In property";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			localTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}
	
	@Test
	public void testWithNoMetaProperty() {
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerWNMetaDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "the Meta property";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			localTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}

}
