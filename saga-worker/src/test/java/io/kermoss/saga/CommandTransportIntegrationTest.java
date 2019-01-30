package io.kermoss.saga;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

import io.kermoss.SagaReactiveBusinessFlowApplication;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.event.OutboundCommandStarted;
import io.kermoss.cmd.domain.repository.CommandRepository;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT , classes = SagaReactiveBusinessFlowApplication.class)
//@TestPropertySource("classpath:application-test.yml")
//@ActiveProfiles("test")
public class CommandTransportIntegrationTest extends KermossIT{

    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @LocalServerPort
    private String serverPort;



    @Test
    @Transactional
    public void shouldDeliverCommand() {
        // given
        final OutboundCommand cmd = new OutboundCommand();
        final String host = String.format("http://localhost:%s/command-executor/commands", serverPort);
        cmd.setDestination(host);
        final OutboundCommand pcmd = commandRepository.save(cmd);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void suspend() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void beforeCommit(boolean readOnly) {
                // when
                final OutboundCommandStarted outboundCommandStarted = new OutboundCommandStarted(
                        new CommandMeta(pcmd.getId(), null, null, null, null, null , null)
                );
                publisher.publishEvent(outboundCommandStarted);

                // then
                final OutboundCommand deliveredCommand = commandRepository.findOutboundCommand(cmd.getId());
                assertEquals(OutboundCommand.Status.DELIVERED, deliveredCommand.getStatus());

            }

            @Override
            public void beforeCompletion() {

            }

            @Override
            public void afterCommit() {

            }

            @Override
            public void afterCompletion(int status) {

            }
        });
    }


    @Test
    public void shouldReceiveCommand() throws InterruptedException {
        final String host = String.format("http://localhost:%s/command-executor/commands", serverPort);
        final RestTemplate restTemplate = new RestTemplate();
        final TransporterCommand outboundCommand = transform(new OutboundCommand());
        restTemplate.postForObject(host, outboundCommand, TransporterCommand.class);

        Thread.sleep(8000);

        final Long commandsCount = commandRepository.count();

        assertEquals(new Long(9L), commandsCount);
    }

    private TransporterCommand transform(OutboundCommand outcmd){
        return new TransporterCommand(outcmd.getSubject(),
                outcmd.getSource(),
                outcmd.getDestination(),
                outcmd.getPGTX() != null ? outcmd.getPGTX() : null,
                outcmd.getLTX(),
                outcmd.getPGTX() == null ? outcmd.getGTX() : null,
                outcmd.getAdditionalHeaders(),
                outcmd.getPayload()
                ,outcmd.getId()
                ,outcmd.getTraceId()
        );
    }
}
