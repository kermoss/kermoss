package io.kermoss.cmd.domain.event;


public class TransportCommandDeliveredEvent extends BaseTransportEvent {
    public TransportCommandDeliveredEvent(final String commandId) {
        super(commandId);
    }
}
