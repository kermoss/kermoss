package io.kermoss.saga.pizzashop.decoder;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.cmd.domain.CommandMeta;
import io.kermoss.cmd.infra.translator.BaseDecoder;
import io.kermoss.saga.pizzashop.event.DeliveryBillArrivedEvent;

public class PayDeliveryServiceDecoder implements BaseDecoder {

    @Override
    public BaseTransactionEvent decode(CommandMeta meta) {
    	DeliveryBillArrivedEvent deliveryBillArrivedEvent = new DeliveryBillArrivedEvent();
        return deliveryBillArrivedEvent;
    }
}
