package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class CommandFailed extends BaseCommandEvent {
    public CommandFailed(CommandMeta meta) {
        super(meta);
    }
}
