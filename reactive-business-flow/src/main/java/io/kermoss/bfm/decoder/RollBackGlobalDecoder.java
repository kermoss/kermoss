package io.kermoss.bfm.decoder;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.infra.translator.BaseDecoder;

public class RollBackGlobalDecoder implements BaseDecoder {

    @Override
    public BaseTransactionEvent decode(CommandMeta meta) {
    	ErrorLocalOccured deliverPizzaStartedEvent = new ErrorLocalOccured();
        return deliverPizzaStartedEvent;
    }
}