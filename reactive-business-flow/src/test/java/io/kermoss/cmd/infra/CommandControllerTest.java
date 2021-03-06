package io.kermoss.cmd.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CommandControllerTest {

    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    
    @Mock
    private CommandMapper commandMapper;
    
    @Mock
    private CommandRepository mockCommandRepository;

    private CommandController commandControllerUnderTest;

    @BeforeEach
    public void setUp() {
        commandControllerUnderTest = new CommandController(commandMapper,mockCommandOrchestrator, mockCommandRepository);
    }

    @Test
    @Disabled
    public void testCreateCommandWhenInboundCommandNotExist() {
        // Setup
        final TransporterCommand command = mock(TransporterCommand.class);

        when(mockCommandOrchestrator.isInboundCommandExist(command.getRefId())).thenReturn(false);

        // Run the test
        final ResponseEntity<?> result = commandControllerUnderTest.createCommand(command);

        // Verify the results
        assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);
        verify(mockCommandOrchestrator).receive(any(InboundCommand.class));
     }

    @Test
    public void testCreateCommandWhenInboundCommandExist() {
        // Setup
        final TransporterCommand command = mock(TransporterCommand.class);

        when(mockCommandOrchestrator.isInboundCommandExist(command.getRefId())).thenReturn(true);

        // Run the test
        final ResponseEntity<?> result = commandControllerUnderTest.createCommand(command);

        // Verify the results
        assertEquals(result.getStatusCode(), HttpStatus.CONFLICT);
        verify(mockCommandOrchestrator, never()).receive(any(InboundCommand.class));

    }

    @Test
    public void testRestartInboundCommand() {
        // Setup
        final String commandId = "commandId";
        final InboundCommand command = mock(InboundCommand.class);

        when(mockCommandRepository.findOneOpt(commandId)).thenReturn(Optional.of(command));
        // Run the test
        final ResponseEntity<?> result = commandControllerUnderTest.restartCommand(commandId);

        // Verify the results
        assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);
        verify(mockCommandOrchestrator).receive(any(InboundCommand.class));
    }

    @Test
    public void testRestartOutboundCommand() {
        // Setup
        final String commandId = "commandId";
        final OutboundCommand command = mock(OutboundCommand.class);

        when(mockCommandRepository.findOneOpt(commandId)).thenReturn(Optional.of(command));
        // Run the test
        final ResponseEntity<?> result = commandControllerUnderTest.restartCommand(commandId);

        // Verify the results
        assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);
        verify(mockCommandOrchestrator).receive(any(OutboundCommand.class));
    }

    @Test
    public void testPrepareCommand() {
        // Setup
        final TransporterCommand command = mock(TransporterCommand.class);

        // Run the test
        final ResponseEntity<?> result = commandControllerUnderTest.prepareCommand(command);

        // Verify the results
        assertEquals(result.getStatusCode(), HttpStatus.ACCEPTED);
        verify(mockCommandOrchestrator).prepare(nullable(InboundCommand.class));
    }
}
