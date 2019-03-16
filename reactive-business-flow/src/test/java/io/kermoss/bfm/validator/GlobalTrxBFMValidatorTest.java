package io.kermoss.bfm.validator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNBDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNCDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNInDummy;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNMetaDummy;
import io.kermoss.bfm.worker.GlobalTransactionWorker;

@RunWith(MockitoJUnitRunner.class)
public class GlobalTrxBFMValidatorTest {

	@Spy
	private GlobalTrxBFMValidator globalTrxBFMValidatorUnderTest;

	@Test
	public void testWithNoProblem() {
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		globalTrxBFMValidatorUnderTest.validate(trxBoundaries);
	}

	@Test
	public void testWithNoBusinessGlobalTransactional() {
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerWNBDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "@BusinessGlobalTransactional annotation must be present on method onStart of io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNBDummy";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			globalTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessage("%s", exMsg);
	}
	
	
	@Test
	public void testWithNoCommitGlobalTransactional() {
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerWNCDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "@CommitBusinessGlobalTransactional annotation must be present on method onComplete of io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerWNCDummy";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			globalTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessage("%s", exMsg);
	}
	
	@Test
	public void testWithNoInProperty() {
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerWNInDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "the In property";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			globalTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}
	
	@Test
	public void testWithNoMetaProperty() {
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerWNMetaDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);

		List<TrxBoundary> trxBoundaries = new ArrayList<>();
		String exMsg = "the Meta property";
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			globalTrxBFMValidatorUnderTest.validate(trxBoundaries);
		}).withMessageContaining(exMsg);
	}

}
