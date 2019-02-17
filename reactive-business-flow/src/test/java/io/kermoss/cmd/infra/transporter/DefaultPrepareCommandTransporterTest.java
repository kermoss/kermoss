package io.kermoss.cmd.infra.transporter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.PreparedCommandTransportException;
import io.kermoss.cmd.infra.transporter.DefaultPrepareCommandTransporter;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.HttpClientStrategy;
import io.kermoss.infra.KermossTxLogger;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPrepareCommandTransporterTest {

    @Mock
    private Environment mockEnvironment;
    @Mock
    private CommandRepository mockCommandRepository;
    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private HttpClientStrategy clientStrategy;
    @Mock
    KermossTxLogger txLogger;
    

    @InjectMocks
    private DefaultPrepareCommandTransporter prepareRestCommandTransporter;


    @Test
    public void testOnEventWhenPostIs2xx() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);
        
        CommonSetup(event, meta, command);
        CommandTransporterStrategy strategy = mock(CommandTransporterStrategy.class);  
        when(clientStrategy.get()).thenReturn(strategy);
        when(strategy.transportPrepareCommand(any())).thenReturn(true);
        when(mockRestTemplate.exchange(anyString(), any(), any(), same(TransporterCommand.class))).thenReturn(entity);
        when(entity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(txLogger.printJsonObject(any())).thenReturn("Command String");
        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(mockCommandOrchestrator).receive(any(OutboundCommand.class));
    }

    @Test(expected = PreparedCommandTransportException.class)
    public void testOnEventWhenPostIsNot2xx() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        CommandTransporterStrategy strategy = mock(CommandTransporterStrategy.class);  
        when(clientStrategy.get()).thenReturn(strategy);
        final ResponseEntity entity = mock(ResponseEntity.class);

        CommonSetup(event, meta, command);
        when(strategy.transportPrepareCommand(any())).thenReturn(false);
        when(mockRestTemplate.exchange(anyString(), any(), any(), same(TransporterCommand.class))).thenReturn(entity);
        when(entity.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(mockCommandOrchestrator, never()).receive(any(OutboundCommand.class));
    }

    @Test
    public void testOnEventWhenCommandStatusNotPrepared() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);

        CommonSetup(event, meta, command);
        when(command.getStatus()).thenReturn(OutboundCommand.Status.STARTED);

        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verify(mockEnvironment, never()).getProperty(anyString());
        verify(command, never()).setSource(anyString());
        verify(mockCommandOrchestrator, never()).receive(any(OutboundCommand.class));
    }

    @Test
    public void testOnEventWhenCommandNotFoundInDb() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);

        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(anyString())).thenReturn(Optional.empty());

        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verify(mockEnvironment, never()).getProperty(anyString());
        verify(command, never()).setSource(anyString());
        verify(mockCommandOrchestrator, never()).receive(any(OutboundCommand.class));
    }

    private void CommonSetup(OutboundCommandPrepared event, CommandMeta meta, OutboundCommand command) {
        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(anyString())).thenReturn(Optional.of(command));
        when(command.getStatus()).thenReturn(OutboundCommand.Status.PREPARED);
        when(mockEnvironment.getProperty(same("kermoss.serviceName"), anyString())).thenReturn("source");
    }

    private void verifyTheCommonResults(OutboundCommand command) {
        verify(mockEnvironment).getProperty(same("kermoss.serviceName"), anyString());
        verify(command).setSource(same("source"));
    }
}
