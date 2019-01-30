package io.kermoss.cmd.infra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandNotFoundException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/command-executor")
public class CommandController {
    private final CommandOrchestrator commandOrchestrator;
    private final GlobalTransactionRepository globalTransactionRepository;
    private final CommandRepository commandRepository;
    private final Logger log = LoggerFactory.getLogger(CommandController.class);
    @Autowired
    public CommandController(
        final CommandOrchestrator commandOrchestrator,
        final GlobalTransactionRepository globalTransactionRepository,
        final CommandRepository commandRepository
    ) {
        this.commandOrchestrator = commandOrchestrator;
        this.globalTransactionRepository = globalTransactionRepository;
        this.commandRepository = commandRepository;
    }

    @PostMapping("/commands")
    public ResponseEntity<?> createCommand(@RequestBody final TransporterCommand command) {
    	
    	if(commandOrchestrator.isInboundCommandExist(command.getRefId())){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        this.commandOrchestrator.receive(this.transform(command));

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
        this.commandOrchestrator.prepare(this.transform(command));
        return new ResponseEntity<>(
                HttpStatus.ACCEPTED
        );
    }

    private InboundCommand transform(TransporterCommand outcmd){
    	
        return InboundCommand.builder()
                .source(outcmd.getSource())
                .subject(outcmd.getSubject())
                .destination(outcmd.getDestination())
                .payload(outcmd.getPayload())
                .PGTX(outcmd.getParentGTX())
                .gTX(outcmd.getChildofGTX())
                .fLTX(outcmd.getFLTX())
                .additionalHeaders(outcmd.getAdditionalHeaders())
                .refId(outcmd.getRefId())
                .trace(outcmd.getTraceId())
                .build();
    }


}
