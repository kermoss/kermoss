package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.domain.AbstractEvent;

public abstract class BaseCommandEvent extends AbstractEvent {
    private final CommandMeta meta;

    @java.beans.ConstructorProperties({"meta"})
    public BaseCommandEvent(CommandMeta meta) {
        this.meta = meta;
    }

    public CommandMeta getMeta() {
        return this.meta;
    }
}
