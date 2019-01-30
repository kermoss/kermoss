package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class InboundCommandPrepared extends CommandPrepared {
    public InboundCommandPrepared(CommandMeta meta) {
        super(meta);
    }
}
