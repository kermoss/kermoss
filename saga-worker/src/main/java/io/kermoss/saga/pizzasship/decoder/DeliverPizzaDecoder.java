package io.kermoss.saga.pizzasship.decoder;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.infra.translator.BaseDecoder;
import io.kermoss.saga.pizzasship.event.BoxArrivedEvent;

public class DeliverPizzaDecoder implements BaseDecoder {

    @Override
    public BaseTransactionEvent decode(CommandMeta meta) {
        BoxArrivedEvent deliverPizzaStartedEvent = new BoxArrivedEvent();
        return deliverPizzaStartedEvent;
    }
}
