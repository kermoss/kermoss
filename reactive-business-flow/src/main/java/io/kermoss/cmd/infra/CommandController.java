package io.kermoss.cmd.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandNotFoundException;

@RestController
@RequestMapping("/command-executor")
public class CommandController {
    private final CommandOrchestrator commandOrchestrator;
    private final CommandRepository commandRepository;
    private final Logger log = LoggerFactory.getLogger(CommandController.class);
    private final CommandMapper commandMapper;
    @Autowired
    public CommandController(
        final CommandMapper commandMapper,		
        final CommandOrchestrator commandOrchestrator,
        final CommandRepository commandRepository
    ) {
    	this.commandMapper=commandMapper; 
        this.commandOrchestrator = commandOrchestrator;
        this.commandRepository = commandRepository;
    }

    @PostMapping("/commands")
    public ResponseEntity<?> createCommand(@RequestBody final TransporterCommand command) {
    	
    	if(commandOrchestrator.isInboundCommandExist(command.getRefId())){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        this.commandOrchestrator.receive(commandMapper.transform(command));

        return new ResponseEntity<>(
                HttpStatus.ACCEPTED
        );
    }

    @PostMapping("/commands/{commandId}/restart")
    public ResponseEntity<?> restartCommand(@PathVariable("commandId") final String commandId) {
        final AbstractCommand command = commandRepository.findOneOpt(commandId).orElseThrow(CommandNotFoundException::new);

        if(command instanceof InboundCommand) {
            commandOrchestrator.receive((InboundCommand) command);
        } else if(command instanceof OutboundCommand){
            commandOrchestrator.receive((OutboundCommand) command);
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/commands/prepare")
    public ResponseEntity<?> prepareCommand(@RequestBody final TransporterCommand command) {
        this.commandOrchestrator.prepare(commandMapper.transform(command));
        return new ResponseEntity<>(
                HttpStatus.ACCEPTED
        );
    }

    


}
