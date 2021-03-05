package io.kermoss.cmd.infra.transporter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.event.OutboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.infra.KermossTxLogger;

public class DefaultRestCommandTransporterTest {

    @Mock
    private ApplicationEventPublisher mockPublisher;
    @Mock
    private CommandRepository mockCommandRepository;
    @Mock
    private Environment mockEnvironment;

    @Mock
    private CommandTransporterStrategy mockStrategy;
    @Mock
    private KermossTxLogger txLogger;

    @InjectMocks
    private DefaultCommandTransporter defaultCommandTransporter;

    @Before
    public void setUp() {
        initMocks(this);
        defaultCommandTransporter= new DefaultCommandTransporter(mockPublisher, mockCommandRepository, mockEnvironment,  mockStrategy, txLogger);
    }

    @Test
    public void testOnEventwhenPostIs200() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);

        CommonSetup(event, meta, command);
        when(mockStrategy.transportCommand(any())).thenReturn(true);
        when(entity.getStatusCode()).thenReturn(HttpStatus.OK);

        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(command).changeStatusToDelivered();
        verify(command, never()).changeStatusToFailed();


    }

    @Test
    public void testOnEventwhenPostIs403() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);
        final HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);

        CommonSetup(event, meta, command);
        when(mockStrategy.transportCommand(any())).thenReturn(true);
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(command).changeStatusToDelivered();
        verify(command, never()).changeStatusToFailed();

    }

    @Test
    public void testOnEventwhenPostNot200And3xx() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);

        CommonSetup(event, meta, command);
        when(mockStrategy.transportCommand(any())).thenReturn(false);
        when(entity.getStatusCode()).thenReturn(HttpStatus.NOT_MODIFIED);

        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(command, never()).changeStatusToDelivered();
        verify(command).changeStatusToFailed();

    }

    @Test
    public void testOnEventwhenPostIs5xx() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);
        final ResponseEntity entity = mock(ResponseEntity.class);
        final HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);

        CommonSetup(event, meta, command);
        when(mockStrategy.transportCommand(any())).thenReturn(false);
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(command, never()).changeStatusToDelivered();
        verify(command).changeStatusToFailed();

    }

    @Test
    public void testOnEventWhenCommandNotDelivred() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);

        CommonSetup(event, meta, command);
        when(mockStrategy.transportCommand(any())).thenReturn(false);

        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verifyTheCommonResults(command);
        verify(command, never()).changeStatusToDelivered();
        verify(command).changeStatusToFailed();

    }

    @Test
    public void testOnEventwhenCommandNotFound() {
        // Setup
        final OutboundCommandStarted event = mock(OutboundCommandStarted.class);
        final CommandMeta meta = mock(CommandMeta.class);
        final OutboundCommand command = mock(OutboundCommand.class);

        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(anyString())).thenReturn(Optional.empty());
        // Run the test
        defaultCommandTransporter.onEvent(event);

        // Verify the results
        verify(command, never()).changeStatusToDelivered();
        verify(command, never()).changeStatusToFailed();
        verify(mockCommandRepository, never()).save(command);


    }


    private void CommonSetup(OutboundCommandStarted event, CommandMeta meta, OutboundCommand command) {
        when(event.getMeta()).thenReturn(meta);
        when(mockCommandRepository.findOutboundCommandOpt(anyString())).thenReturn(Optional.of(command));
        when(command.getStatus()).thenReturn(OutboundCommand.Status.STARTED);
        when(mockEnvironment.getProperty(same("kermoss.service-name"), anyString())).thenReturn("source");
    }

    private void verifyTheCommonResults(OutboundCommand command) {
        verify(mockEnvironment).getProperty(same("kermoss.service-name"), anyString());
        verify(command).setSource(same("source"));
        verify(mockCommandRepository).save(command);
    }
}
