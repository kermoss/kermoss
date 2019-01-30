package io.kermoss.cmd.infra.transporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.PreparedCommandTransportException;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.infra.KermossTxLogger;


@Service
public class DefaultPrepareCommandTransporter extends AbstractCommandTransporter<OutboundCommandPrepared> {
    private final Logger log = LoggerFactory.getLogger(DefaultPrepareCommandTransporter.class);
    private final Environment environment;
    private final CommandRepository commandRepository;
    private final CommandTransporterStrategy strategy;
    private final CommandOrchestrator commandOrchestrator;
    private final KermossTxLogger txLogger;


    @Autowired
    public DefaultPrepareCommandTransporter(
        final ApplicationEventPublisher publisher,
        final Environment environment,
        final CommandRepository commandRepository,
        final CommandOrchestrator commandOrchestrator,
        final CommandTransporterStrategy strategy,
        final KermossTxLogger txLogger
    ) {
        super(publisher);
        this.environment = environment;
        this.commandRepository = commandRepository;
        this.commandOrchestrator = commandOrchestrator;
        this.strategy = strategy;
        this.txLogger = txLogger;
    }

    @Override
    @Transactional
    @EventListener
    public void onEvent(final OutboundCommandPrepared event) {
        log.info("calling DefaultPrepareCommandTransporter by event {}", event.getClass().getName());
        this.commandRepository.findOutboundCommandOpt(event.getMeta().getCommandId()).ifPresent(outboundCommand -> {
            // if Prepared then deliver
            if(outboundCommand.getStatus().equals(OutboundCommand.Status.PREPARED)) {
                final String source = environment.getProperty(
                    "kermoss.serviceName",
                    environment.getProperty("spring.application.name")
                );
                outboundCommand.setSource(source);

                if(!this.strategy.transportPrepareCommand(transform(outboundCommand))) {
                    log.info("Failure Transport Prepared Command over HTTP: {}", txLogger.printJsonObject(outboundCommand));
                    throw new PreparedCommandTransportException(outboundCommand.getId());
                }
                log.info("Success Transport Prepared Command over HTTP: {}", txLogger.printJsonObject(outboundCommand));
                // This part is needed to change status of Global transaction from PREPARED to started
                OutboundCommand command = OutboundCommand.builder()
                    .subject(outboundCommand.getSubject())
                    .gTX(outboundCommand.getGTX())
                    .pGTX(outboundCommand.getPGTX())
                    .lTX(outboundCommand.getLTX())
                    .fLTX(outboundCommand.getFLTX())
                    .destination(outboundCommand.getDestination())
                    .trace(outboundCommand.getTraceId())
                    .build();
                log.info("Changing OutboundCommand status from Prepared to Started: {}", txLogger.printJsonObject(outboundCommand));
                commandOrchestrator.receive(command);
                txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for Prepared OutboundCommand: {}", txstatus, txLogger.printJsonObject(outboundCommand)));

            }
        });
    }
}
