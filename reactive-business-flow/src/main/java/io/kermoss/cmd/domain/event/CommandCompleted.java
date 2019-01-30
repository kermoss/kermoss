package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class CommandCompleted extends BaseCommandEvent {
    public CommandCompleted(final CommandMeta meta) {
        super(meta);
    }
}
