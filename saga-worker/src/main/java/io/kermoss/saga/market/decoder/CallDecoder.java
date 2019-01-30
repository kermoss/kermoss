package io.kermoss.saga.market.decoder;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.infra.translator.BaseDecoder;
import io.kermoss.saga.market.event.PhoneRingingEvent;

public class CallDecoder implements BaseDecoder {

    @Override
    public BaseTransactionEvent decode(CommandMeta meta) {
        PhoneRingingEvent phoneRingingEvent = new PhoneRingingEvent();
        return phoneRingingEvent;
    }
}
