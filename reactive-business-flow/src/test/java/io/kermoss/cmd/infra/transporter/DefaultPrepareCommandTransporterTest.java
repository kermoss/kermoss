package io.kermoss.cmd.infra.transporter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.app.CommandOrchestratorImpl;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.HttpClientStrategy;
import io.kermoss.infra.KermossTxLogger;

//@ExtendWith(MockitoExtension.class)
@Disabled
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

    
    @BeforeEach
    public void setUp() {
        initMocks(this);

    }

    @Test
    public void testOnEventWhenPostIs2xx() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        //when(event.getId()).thenReturn(UUID.randomUUID().toString());
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);
        
        CommonSetup(event, meta, command);
        CommandTransporterStrategy strategy = mock(CommandTransporterStrategy.class);  
        when(clientStrategy.get()).thenReturn(strategy);
        when(strategy.transportPrepareCommand(any())).thenReturn(true);
        when(mockRestTemplate.exchange(nullable(String.class), any(), any(), same(TransporterCommand.class))).thenReturn(entity);
        when(entity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(txLogger.printJsonObject(any())).thenReturn("Command String");
        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(mockCommandOrchestrator).receive(any(OutboundCommand.class));
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
        verify(mockEnvironment, never()).getProperty(nullable(String.class));
        verify(command, never()).setSource(nullable(String.class));
        verify(mockCommandOrchestrator, never()).receive(any(OutboundCommand.class));
    }

    @Test
    public void testOnEventWhenCommandNotFoundInDb() {
        // Setup
        final OutboundCommandPrepared event = mock(OutboundCommandPrepared.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);

        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(nullable(String.class))).thenReturn(Optional.empty());

        // Run the test
        prepareRestCommandTransporter.onEvent(event);

        // Verify the results
        verify(mockEnvironment, never()).getProperty(nullable(String.class));
        verify(command, never()).setSource(nullable(String.class));
        verify(mockCommandOrchestrator, never()).receive(any(OutboundCommand.class));
    }

    private void CommonSetup(OutboundCommandPrepared event, CommandMeta meta, OutboundCommand command) {
        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(nullable(String.class))).thenReturn(Optional.of(command));
        when(command.getStatus()).thenReturn(OutboundCommand.Status.PREPARED);
        when(mockEnvironment.getProperty(same("kermoss.service-name"), nullable(String.class))).thenReturn("source");
        when(mockEnvironment.getProperty(same("spr"), nullable(String.class))).thenReturn("source");
    }

    private void verifyTheCommonResults(OutboundCommand command) {
        verify(command).setSource(same("source"));
    }
}
