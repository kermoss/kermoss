package io.kermoss.cs.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import io.kermoss.cs.domain.Command;
import io.kermoss.cs.service.CommandFeignClient;
import io.kermoss.cs.service.CommandService;

import java.util.List;

@Configuration
public class CommandTask {
    private static final Logger log = LoggerFactory.getLogger(CommandTask.class);
    private final CommandService commandService;
    private final CommandFeignClient commandFeignClient;

    @Autowired
    public CommandTask(
        final CommandService commandService,
        final CommandFeignClient commandFeignClient
    ) {
        this.commandService = commandService;
        this.commandFeignClient = commandFeignClient;
    }

    @Scheduled(fixedRateString = "${kermoss.cs.config.rate}")
    public void scheduledCommandsStarter() {
        log.info("Start Processing Failed Commands");
        final List<Command> commands = commandService.findFailedCommands();
        log.info("Fetch {} Failed Commands", commands.size());

        commands.parallelStream().forEach(c -> commandFeignClient.restartCommand(c.getId()));

        log.info("Finish Processing Failed Commands");
    }
}
