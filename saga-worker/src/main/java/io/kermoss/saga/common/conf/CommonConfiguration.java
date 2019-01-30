package io.kermoss.saga.common.conf;


import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.kermoss.domain.DecoderRegistry;
import io.kermoss.saga.market.decoder.CallDecoder;
import io.kermoss.saga.pizzashop.decoder.PayDeliveryServiceDecoder;
import io.kermoss.saga.pizzasship.decoder.DeliverPizzaDecoder;

@Component
public class CommonConfiguration extends DecoderRegistry {

    @PostConstruct
    public void setup() {
        PayDeliveryServiceDecoder decoder0 = new PayDeliveryServiceDecoder();
        this.put("pay-delivery-service", decoder0);
        DeliverPizzaDecoder decoder1 = new DeliverPizzaDecoder();
        this.put("deliver-pizza", decoder1);
        CallDecoder callDecoder = new CallDecoder();
        this.put("call-market", callDecoder);
    }
}
