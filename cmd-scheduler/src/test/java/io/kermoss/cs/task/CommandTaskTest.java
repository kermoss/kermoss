package io.kermoss.cs.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.cs.domain.Command;
import io.kermoss.cs.service.CommandFeignClient;
import io.kermoss.cs.service.CommandService;
import io.kermoss.cs.task.CommandTask;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandTaskTest {

    @Mock
    private CommandService commandService;
    @Mock
    private CommandFeignClient commandFeignClient;

    @InjectMocks
    private CommandTask commandTask;

    @Test
    public void scheduledCommandsStarterShouldFetchFailedCommands() {
        final List<Command> failedCommands = Arrays.asList();

        when(commandService.findFailedCommands()).thenReturn(failedCommands);

        commandTask.scheduledCommandsStarter();

        verify(commandService).findFailedCommands();
    }

    @Test
    public void scheduledCommandsStarterShouldSendRestartRequest() {
        final String commandId = UUID.randomUUID().toString();
        final Command cmd = new Command(commandId, "FAILED");
        final List<Command> failedCommands = Arrays.asList(cmd);

        when(commandService.findFailedCommands()).thenReturn(failedCommands);

        commandTask.scheduledCommandsStarter();

        verify(commandFeignClient).restartCommand(same(commandId));
    }
}