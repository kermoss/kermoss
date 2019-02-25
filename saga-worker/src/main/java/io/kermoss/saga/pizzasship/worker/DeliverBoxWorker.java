package io.kermoss.saga.pizzasship.worker;

import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.common.contract.ToDilever;
import io.kermoss.saga.pizzasship.event.BoxArrivedEvent;
import io.kermoss.saga.pizzasship.event.BoxDeliverProcessingEvent;
import io.kermoss.saga.pizzasship.event.BoxDelivredEvent;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Component
@Profile({"single","ship"})
public class DeliverBoxWorker extends GlobalTransactionWorker<BoxArrivedEvent, BoxDelivredEvent> {

    public DeliverBoxWorker() {
        super(new WorkerMeta("DeliverBoxService"));
    }

    @Override
    @BusinessGlobalTransactional
    public GlobalTransactionStepDefinition onStart(BoxArrivedEvent boxArrivedEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(boxArrivedEvent)
                .meta(this.meta)
                .blow(Stream.of(new BoxDeliverProcessingEvent()))
                .receive(ToDilever.class, x -> System.out.println(x.toString()))
                .build();
    }

    @Override
    @CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(BoxDelivredEvent onCompleteEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(onCompleteEvent)
                .meta(this.meta)
                .build();
    }
}
