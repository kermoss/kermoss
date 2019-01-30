package io.kermoss.cmd.domain.event;


public class TransportCommandFailedEvent extends BaseTransportEvent {
    public TransportCommandFailedEvent(final String commandId) {
        super(commandId);
    }
}
