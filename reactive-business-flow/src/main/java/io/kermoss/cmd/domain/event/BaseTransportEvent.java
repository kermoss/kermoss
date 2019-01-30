package io.kermoss.cmd.domain.event;

import io.kermoss.domain.AbstractEvent;

public abstract class BaseTransportEvent extends AbstractEvent {
    private final String commandId;

    @java.beans.ConstructorProperties({"commandId"})
    public BaseTransportEvent(String commandId) {
        this.commandId = commandId;
    }

    public String getCommandId() {
        return this.commandId;
    }
}
