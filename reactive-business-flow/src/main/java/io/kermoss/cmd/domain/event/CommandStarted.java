package io.kermoss.cmd.domain.event;


import io.kermoss.cmd.domain.CommandMeta;

public abstract class CommandStarted extends BaseCommandEvent {
    public CommandStarted(final CommandMeta meta) {
        super(meta);
    }
}
