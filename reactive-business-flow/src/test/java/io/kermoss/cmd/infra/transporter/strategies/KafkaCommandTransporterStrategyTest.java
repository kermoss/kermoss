package io.kermoss.cmd.infra.transporter.strategies;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

@RunWith(MockitoJUnitRunner.class)
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
		Assert.assertTrue(transportCommand);
    
    }
    
    @Test
    public void whenKafkaProducerFailTransportCommandReturnFalse() throws InterruptedException, ExecutionException {
    	when(kermossProperties.getKafkaDestination(anyString())).thenReturn("io.kermoss.topic.pizza-shop");	
    	ListenableFuture mock = mock(ListenableFuture.class);
    	TransporterCommand command =mock(TransporterCommand.class);
		ListenableFuture<SendResult<String, TransporterCommand>> send = this.kafkaTemplate.send("io.kermoss.topic.pizza-shop", command);
		when(send).thenThrow(InterruptedException.class);
		Boolean transportCommand = kafkaCommandTransporterStrategy.transportCommand(command);
		Assert.assertFalse(transportCommand);
    
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
		Assert.assertFalse(transportCommand);
    
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
		Assert.assertTrue(transportCommand);
    
    }



}