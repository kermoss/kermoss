package io.kermoss.cmd.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.event.InboundCommandPrepared;
import io.kermoss.cmd.domain.event.InboundCommandStarted;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.event.OutboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.CommandUnmarchelingException;
import io.kermoss.cmd.exception.DecoderException;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.infra.KermossTxLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
public class CommandOrchestratorImpl implements CommandOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(CommandOrchestratorImpl.class);
    private final CommandRepository commandRepository;
    private final ApplicationEventPublisher publisher;
    private final BubbleCache bubbleCache;
    private final ObjectMapper mapper;
    private final KermossTxLogger txLogger;
    private Environment environment;

    @Autowired
    public CommandOrchestratorImpl(
        final CommandRepository commandRepository,
        final ApplicationEventPublisher publisher,
        final BubbleCache bubbleCache,
        final ObjectMapper mapper,
        final Environment environment,
        final KermossTxLogger txLogger
    ) {
        this.commandRepository = commandRepository;
        this.publisher = publisher;
        this.bubbleCache = bubbleCache;
        this.mapper = mapper;
        this.environment = environment;
        this.txLogger = txLogger;
    }

    
    
    private void receiveCommand(final AbstractCommand command, final Consumer<CommandMeta> consumer) {
        AbstractCommand persistedCommand = null;
        if(!commandRepository.exists(command.getId())) {

            persistedCommand = this.commandRepository.save(command);
        } else {
            persistedCommand = commandRepository.findOne(command.getId());
        }
        consumer.accept(persistedCommand.buildMeta());
    }

    @Override
    public void receive(final InboundCommand command) {
        txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for InboundCommand: {}", txstatus, txLogger.printJsonObject(command)));
        this.receiveCommand(
            command,
            (meta) -> this.publisher.publishEvent(new InboundCommandStarted(meta))
        );
    }

    @Override
    public void receive(final OutboundCommand command) {
        AbstractCommand cmd = this.setCommandSource(command);
        txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for OutboundCommand: {}", txstatus, txLogger.printJsonObject(cmd)));
        this.receiveCommand(
                cmd,
                (meta) -> this.publisher.publishEvent(new OutboundCommandStarted(meta))
        );
    }

    @Override
    public void receive(BaseTransactionCommand command) {
        AbstractCommand cmd = this.setCommandSource(this.commandMapper(command));
        txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for OutboundCommand: {}", txstatus, txLogger.printJsonObject(cmd)));
        this.receiveCommand(
                cmd,
                (meta) -> this.publisher.publishEvent(new OutboundCommandStarted(meta))
        );
    }

    @Override
    public void prepare(BaseTransactionCommand command) {
        OutboundCommand obc = this.commandMapper(command);
        obc.setStatus(OutboundCommand.Status.PREPARED);
        AbstractCommand cmd = this.setCommandSource(obc);
        txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for Prepared OutboundCommand: {}", txstatus, txLogger.printJsonObject(cmd)));
        this.receiveCommand(
                cmd,
                (meta) -> this.publisher.publishEvent(new OutboundCommandPrepared(obc.buildMeta()))
        );
    }

    @Override
    public void prepare(InboundCommand command) {
        command.setStatus(InboundCommand.Status.PREPARED);
        txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for Prepared InboundCommand: {}", txstatus, txLogger.printJsonObject(command)));
        this.receiveCommand(
                command,
                (meta) -> this.publisher.publishEvent(new InboundCommandPrepared(command.buildMeta()))
        );
    }

    @Override
    public boolean isInboundCommandExist(String refId) {
        if (commandRepository.findByRefId(refId).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public boolean isInboundCommandWithStatusExist(String refId, InboundCommand.Status status) {
        if (commandRepository.findByIdAndStatus(refId, status).isPresent()){
            return true;
        }
        return false;
    }

    @Override
    public <P> Optional<P> retreive(String eventId, Class<P> target){
        Optional<P> payload = Optional.empty();

        Optional<BubbleMessage> bubbleMessage = (bubbleCache.getBubble(eventId));
        if (bubbleMessage.isPresent()){
                AbstractCommand c = commandRepository.findOne(bubbleMessage.get().getCommandId());
                try {
                    payload = Optional.of(mapper.readValue(c.getPayload(), target));

                } catch (Exception e) {
                    //TODO add better handler
                    throw new CommandUnmarchelingException(e.getMessage());
                }
        }

        return payload;
    }

    OutboundCommand commandMapper(BaseTransactionCommand baseTransactionCommand){
        //TODO remove get
        BubbleMessage bubbleMessage = bubbleCache.getBubble(baseTransactionCommand.getId()).get();
        String payload = null;
        try {
            payload = mapper.writeValueAsString(baseTransactionCommand.getPayload());
        } catch (JsonProcessingException e) {
            throw new DecoderException(e.getMessage());
        }
        return OutboundCommand.builder()
                .additionalHeaders(baseTransactionCommand.getHeader())
                .payload(payload)
                .subject(baseTransactionCommand.getSubject())
                .destination(baseTransactionCommand.getDestination())
                .gTX(bubbleMessage.getGLTX())
                .pGTX(bubbleMessage.getPGTX())
                .fLTX(bubbleMessage.getFLTX())
                .lTX(bubbleMessage.getLTX())
                .trace(bubbleMessage.getTraceId())
                .build();
    }

    private AbstractCommand setCommandSource(AbstractCommand command){
        final String source = environment.getProperty(
                "kermoss.serviceName",
                environment.getProperty("spring.application.name")
        );
        command.setSource(source);
        return command;
    }
}
