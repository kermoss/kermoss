package io.kermoss.cmd.infra;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.infra.CommandController;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommandControllerTest {

    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    @Mock
    private GlobalTransactionRepository mockGlobalTransactionRepository;
    @Mock
    private CommandRepository mockCommandRepository;

    private CommandController commandControllerUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        commandControllerUnderTest = new CommandController(mockCommandOrchestrator, mockGlobalTransactionRepository, mockCommandRepository);
    }

    @Test
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
        verify(mockCommandOrchestrator).prepare(any(InboundCommand.class));
    }
}
