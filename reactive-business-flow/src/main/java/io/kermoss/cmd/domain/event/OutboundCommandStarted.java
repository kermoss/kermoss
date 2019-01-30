package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class OutboundCommandStarted extends CommandStarted {
    public OutboundCommandStarted(CommandMeta meta) {
        super(meta);
    }
}
