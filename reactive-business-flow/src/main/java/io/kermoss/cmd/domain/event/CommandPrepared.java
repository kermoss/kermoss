package io.kermoss.cmd.domain.event;


import io.kermoss.cmd.domain.CommandMeta;

public abstract class CommandPrepared extends BaseCommandEvent {

    public CommandPrepared(final CommandMeta meta) {
        super(meta);
    }


}
