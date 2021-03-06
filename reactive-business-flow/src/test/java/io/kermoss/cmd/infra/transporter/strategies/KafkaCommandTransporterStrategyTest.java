package io.kermoss.cmd.infra.transporter.strategies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

@ExtendWith(MockitoExtension.class)
@Disabled
public class KafkaCommandTransporterStrategyTest {
	@InjectMocks
    private KafkaCommandTransporterStrategy kafkaCommandTransporterStrategy;

    @Mock
    private KafkaTemplate<String, TransporterCommand> kafkaTemplate;
    
    @Mock
    private KermossProperties kermossProperties;

    
    @Test
    public void whenKafkaProducerWorkTransportCommandReturnTrue() throws InterruptedException, ExecutionException {
    	when(kermossProperties.getKafkaDestination(anyString())).thenReturn("io.kermoss.topic.pizza-shop");	
    	ListenableFuture mock = mock(ListenableFuture.class);
    	TransporterCommand command =mock(TransporterCommand.class);
		ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send("io.kermoss.topic.pizza-shop", command);
		when(send).thenReturn(mock);
		Boolean transportCommand = kafkaCommandTransporterStrategy.transportCommand(command);
		assertTrue(transportCommand);
    
    }
    
    @Test
    public void whenKafkaProducerFailTransportCommandReturnFalse() throws InterruptedException, ExecutionException {
    	when(kermossProperties.getKafkaDestination(anyString())).thenReturn("io.kermoss.topic.pizza-shop");	
    	ListenableFuture mock = mock(ListenableFuture.class);
    	TransporterCommand command =mock(TransporterCommand.class);
		ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send("io.kermoss.topic.pizza-shop", command);
		when(send).thenThrow(InterruptedException.class);
		Boolean transportCommand = kafkaCommandTransporterStrategy.transportCommand(command);
		assertFalse(transportCommand);
    
    }
    
    @Test
    public void whenKafkaGetProducerFailTransportCommandReturnFalse() throws InterruptedException, ExecutionException {
    	when(kermossProperties.getKafkaDestination(anyString())).thenReturn("io.kermoss.topic.pizza-shop");	
    	ListenableFuture mock = mock(ListenableFuture.class);
    	TransporterCommand command =mock(TransporterCommand.class);
    	ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send("io.kermoss.topic.pizza-shop", command);
		when(send).thenReturn(mock);
		when(mock.get()).thenThrow(ExecutionException.class);
		Boolean transportCommand = kafkaCommandTransporterStrategy.transportCommand(command);
		assertFalse(transportCommand);
    
    }
    
    @Test
    public void whenTransportCommandUseGetMethod() throws InterruptedException, ExecutionException {
    	when(kermossProperties.getKafkaDestination(anyString())).thenReturn("io.kermoss.topic.pizza-shop");	
    	ListenableFuture mock = mock(ListenableFuture.class);
    	TransporterCommand command =mock(TransporterCommand.class);
    	ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send("io.kermoss.topic.pizza-shop", command);
		when(send).thenReturn(mock);
		Boolean transportCommand = kafkaCommandTransporterStrategy.transportCommand(command);
		verify(mock).get();
		assertTrue(transportCommand);
    
    }



}