package io.kermoss.cmd.infra.transporter;


import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.cmd.domain.OutboundCommand;
import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.domain.event.BaseCommandEvent;

public abstract class AbstractCommandTransporter<T extends BaseCommandEvent> {
    protected ApplicationEventPublisher publisher;

    @java.beans.ConstructorProperties({"publisher"})
    public AbstractCommandTransporter(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public abstract void onEvent(final T event);

    protected TransporterCommand transform(OutboundCommand outcmd){
        return new TransporterCommand(outcmd.getSubject(),
                outcmd.getSource(),
                outcmd.getDestination(),
                outcmd.getPGTX() != null ? outcmd.getPGTX() : null,
                outcmd.getLTX(),
                outcmd.getPGTX() == null ? outcmd.getGTX() : null,
                outcmd.getAdditionalHeaders(),
                outcmd.getPayload(),
                outcmd.getId(),
                outcmd.getTraceId()
                );
    }

}
