package io.kermoss.cmd.app;

import java.util.List;
import java.util.Optional;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;

public interface CommandOrchestrator {
    void receive(final InboundCommand command);
    void receive(final OutboundCommand command);
    void receive(final BaseTransactionCommand command);
    void prepare(final BaseTransactionCommand command);
    void prepare(final InboundCommand command);
    <P> Optional<P> retreive(String eventId, Class<P> target);
    boolean isInboundCommandExist(final String refId);
    boolean isInboundCommandWithStatusExist(final String refId, final InboundCommand.Status status);
}
