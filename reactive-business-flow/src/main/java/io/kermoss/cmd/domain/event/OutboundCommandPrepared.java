package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class OutboundCommandPrepared extends CommandPrepared {
    public OutboundCommandPrepared(CommandMeta meta) {
        super(meta);
    }
}
