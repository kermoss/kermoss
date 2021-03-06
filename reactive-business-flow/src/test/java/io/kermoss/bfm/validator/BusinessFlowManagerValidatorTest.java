package io.kermoss.bfm.validator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.validator.dummie.gtx.OrderPizzaStateWorkerDummy;
import io.kermoss.bfm.validator.dummie.ltx.ChefCookingServiceWorkerDummy;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.LocalTransactionWorker;

@ExtendWith(MockitoExtension.class)
public class BusinessFlowManagerValidatorTest {
 
	@Spy
	private LocalTrxBFMValidator localTrxBFMValidatorUnderTest;
	@Spy
	private GlobalTrxBFMValidator globalTrxBFMValidatorUnderTest;
	
	@Spy
	BusinessFlowManagerValidator businessFlowManagerValidatorUnderTest;
	
	@Test
	public void testInit(){
		List<GlobalTransactionWorker<? extends BaseGlobalTransactionEvent, ? extends BaseGlobalTransactionEvent>> gtxWorkers = new ArrayList<>();
		gtxWorkers.add(new OrderPizzaStateWorkerDummy());
		globalTrxBFMValidatorUnderTest.setGtxWorkers(gtxWorkers);
		List<LocalTransactionWorker<? extends BaseTransactionEvent, ? extends BaseTransactionEvent, ? extends ErrorOccured>> ltxWorkers = new ArrayList<>();
		ltxWorkers.add(new ChefCookingServiceWorkerDummy());
		localTrxBFMValidatorUnderTest.setLtxWorkers(ltxWorkers);
		
		List<TransactionBFMValidator> trxBFMValidator=new ArrayList<>();
		trxBFMValidator.add(globalTrxBFMValidatorUnderTest);
		trxBFMValidator.add(localTrxBFMValidatorUnderTest);
		businessFlowManagerValidatorUnderTest.setTrxBFMValidator(trxBFMValidator);
		;
		assertThatExceptionOfType(BusinessFlowManagerValidatorException.class).isThrownBy(() -> {
			businessFlowManagerValidatorUnderTest.init();
		}).withMessageContaining("the Business Transaction with class");
		
	}
	
}
