package io.kermoss.cmd.infra.transporter;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import feign.Client;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.event.OutboundCommandPrepared;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.cmd.exception.PreparedCommandTransportException;
import io.kermoss.cmd.infra.transporter.strategies.CommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.FeignCommandTransporterStrategy;
import io.kermoss.cmd.infra.transporter.strategies.HttpClientStrategy;
import io.kermoss.cmd.infra.transporter.strategies.RestCommandTransporterStrategy;
import io.kermoss.infra.KermossTxLogger;
import io.kermoss.props.KermossProperties;
import io.kermoss.props.Layer;


@Service
public class DefaultPrepareCommandTransporter extends AbstractCommandTransporter<OutboundCommandPrepared> {
    private final Logger log = LoggerFactory.getLogger(DefaultPrepareCommandTransporter.class);
    private final Environment environment;
    private final CommandRepository commandRepository;
    private final HttpClientStrategy clientStrategy;    
    private final CommandOrchestrator commandOrchestrator;
    private final KermossTxLogger txLogger;

    @Autowired
    public DefaultPrepareCommandTransporter(
    	final ApplicationEventPublisher publisher,
        final Environment environment,
        final CommandRepository commandRepository,
        final CommandOrchestrator commandOrchestrator,
        final KermossTxLogger txLogger,
        final HttpClientStrategy clientStrategy
    ) {
        super(publisher);
        this.environment = environment;
        this.commandRepository = commandRepository;
        this.commandOrchestrator = commandOrchestrator;
        this.txLogger = txLogger;
        this.clientStrategy=clientStrategy;
    }
    
     

    @Override
    @Transactional
    @EventListener
    public void onEvent(final OutboundCommandPrepared event) {
        log.info("calling DefaultPrepareCommandTransporter by event {}", event.getClass().getName());
        CommandTransporterStrategy commandTransporterStrategy = this.clientStrategy.get();
        this.commandRepository.findOutboundCommandOpt(event.getMeta().getCommandId()).ifPresent(outboundCommand -> {
            // if Prepared then deliver
            if(outboundCommand.getStatus().equals(OutboundCommand.Status.PREPARED)) {
                final String source = environment.getProperty(
                    "kermoss.service-name",
                    environment.getProperty("spring.application.name")
                );
                outboundCommand.setSource(source);

                if(!commandTransporterStrategy.transportPrepareCommand(transform(outboundCommand))) {
                    log.info("Failure Transport Prepared Command over HTTP: {}", txLogger.printJsonObject(outboundCommand));
                    throw new PreparedCommandTransportException(outboundCommand.getId());
                }
                log.info("Success Transport Prepared Command over HTTP: {}", txLogger.printJsonObject(outboundCommand));
                // This part is needed to change status of Global transaction from PREPARED to started
                
                log.info("Changing OutboundCommand status from Prepared to Started: {}", txLogger.printJsonObject(outboundCommand));
                commandOrchestrator.receive(outboundCommand);
                txLogger.logTransactionState(txstatus -> log.info( "Transaction {} for Prepared OutboundCommand: {}", txstatus, txLogger.printJsonObject(outboundCommand)));

            }
        });
    }
}
