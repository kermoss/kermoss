package io.kermoss.bfm.app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

@ExtendWith(MockitoExtension.class)
public class BusinessFlowTest {
	@Mock
	ApplicationEventPublisher publisher;
	@Mock
	GlobalTransactionRepository globalTransactionRepository;
    @Mock
    BubbleCache bubbleCache;
    @InjectMocks
    BusinessFlow businessFlowUt;
    
    @Test
    public void testAccess() {
    	BaseTransactionEvent mockEvent = mock(BaseTransactionEvent.class);
    	GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
    	
    	String gtx = "2c95e0816916dd9a016916de13d90002";
		when(globalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));
    	businessFlowUt.access(gtx, mockEvent);
    	verify(globalTransactionRepository, times(1)).findById(gtx);
    	
    }
    @Test
    public void testAccessPublishEvent() {
    	BaseTransactionEvent mockEvent = mock(BaseTransactionEvent.class);
    	GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
    	
    	String gtx = "2c95e0816916dd9a016916de13d90002";
		when(globalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));
    	businessFlowUt.access(gtx, mockEvent);
    	verify(publisher, times(1)).publishEvent(mockEvent);
    	
    }

}