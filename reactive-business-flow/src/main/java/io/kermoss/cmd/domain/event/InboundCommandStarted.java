package io.kermoss.cmd.domain.event;

import io.kermoss.cmd.domain.CommandMeta;

public class InboundCommandStarted extends CommandStarted {
    public InboundCommandStarted(CommandMeta meta) {
        super(meta);
    }
}
