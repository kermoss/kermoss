package io.kermoss.saga.market.worker;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.market.event.HangOffEvent;
import io.kermoss.saga.market.event.PhoneRingingEvent;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Component
@Profile({"single","market"})
public class SellerServiceWorker extends GlobalTransactionWorker<PhoneRingingEvent, HangOffEvent> {

    public SellerServiceWorker() {
        super(new WorkerMeta("SellerService"));
    }
    @Override
    @BusinessGlobalTransactional
    public GlobalTransactionStepDefinition onStart(PhoneRingingEvent phoneRingingEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(phoneRingingEvent)
                .process(Optional.of(() -> {
                    System.out.println("Your ingrendients are available!");
                    return null;
                }))
                .meta(this.meta)
                .build();
    }

    @Override
    @CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(HangOffEvent onCompleteEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(onCompleteEvent)
                .meta(this.meta)
                .build();
    }
}
