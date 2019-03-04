package io.kermoss.bfm.worker.rollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.bfm.decoder.RollBackGlobalDecoder;
import io.kermoss.bfm.event.ErrorGlobalOccured;
import io.kermoss.bfm.event.GlobalRollbackOccured;
import io.kermoss.bfm.event.GlobalTransactionLevelRollbacked;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.infra.translator.LanguageTranslator;
import io.kermoss.props.KermossProperties;

@RunWith(MockitoJUnitRunner.class)
public class RollBackGlobalWorkerTest {

	@Mock
	private KermossProperties kermossProperties;
	@Mock
	private LanguageTranslator languageTranslator;

	@InjectMocks
	RollBackGlobalWorker rollBackGlobalWorkerUnderTest;

   
   @Test
   public void testInitUseLanguageTranslator(){
	   rollBackGlobalWorkerUnderTest.init();
	   verify(languageTranslator).registerDecoder(anyString(),any(RollBackGlobalDecoder.class));
   }
   
   
   @Test
   public void testOnStart(){
	   when(kermossProperties.getSources()).thenReturn(mock(HashMap.class));
	   LocalTransactionStepDefinition onStart = rollBackGlobalWorkerUnderTest.onStart(new GlobalRollbackOccured());
	   verify(kermossProperties.getSources(),times(1)).keySet();
	   assertThat(onStart.getMeta().getTransactionName()).isEqualTo("RollBackGlobalService");
   }

   @Test
   public void testOnNext(){
	   LocalTransactionStepDefinition onNext = rollBackGlobalWorkerUnderTest.onNext(new GlobalTransactionLevelRollbacked());
       assertThat(onNext.getIn()).isNotNull();
       assertThat(onNext.getMeta().getTransactionName()).isEqualTo("RollBackGlobalService");
   }
   
   @Test
   public void testOnError() {
	   LocalTransactionStepDefinition onError = rollBackGlobalWorkerUnderTest.onError(new ErrorGlobalOccured());
	   assertThat(onError.getIn()).isNotNull();
	   assertThat(onError.getMeta().getTransactionName()).isEqualTo("RollBackGlobalService");
   }

}