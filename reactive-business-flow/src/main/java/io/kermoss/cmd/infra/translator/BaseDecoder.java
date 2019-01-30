package io.kermoss.cmd.infra.translator;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;

public interface BaseDecoder {
    BaseTransactionEvent decode(final CommandMeta meta);
}
