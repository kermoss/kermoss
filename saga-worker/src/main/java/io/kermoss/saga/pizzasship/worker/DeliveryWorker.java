package io.kermoss.saga.pizzasship.worker;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.LocalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.common.contract.Bill;
import io.kermoss.saga.pizzasship.cmd.PayBillCommand;
import io.kermoss.saga.pizzasship.event.BoxDeliverFinishedEvent;
import io.kermoss.saga.pizzasship.event.BoxDeliverProcessingEvent;
import io.kermoss.saga.pizzasship.event.BoxDelivredEvent;
import io.kermoss.trx.app.annotation.BusinessLocalTransactional;
import io.kermoss.trx.app.annotation.RollBackBusinessLocalTransactional;
import io.kermoss.trx.app.annotation.SwitchBusinessLocalTransactional;

@Component
@Profile({"single","ship"})
public class DeliveryWorker extends LocalTransactionWorker<BoxDeliverProcessingEvent, BoxDeliverFinishedEvent,ErrorLocalOccured> {

    private static final Logger log = LoggerFactory.getLogger(DeliveryWorker.class);

    public DeliveryWorker() {
        super(new WorkerMeta("BoxDelivery", "DeliverBoxService"));    }

    @Override
    @BusinessLocalTransactional
    public LocalTransactionStepDefinition onStart(BoxDeliverProcessingEvent onStartEvent) {
        return LocalTransactionStepDefinition.builder()
                .in(onStartEvent)
                .blow(Stream.of(new BoxDeliverFinishedEvent()))
                .process(deliveringBoxSupplier())
                .meta(this.meta)
                .build();
    }

    @Override
    @SwitchBusinessLocalTransactional
    public LocalTransactionStepDefinition onNext(BoxDeliverFinishedEvent onNextEvent) {

    	PayBillCommand cmd = new PayBillCommand("pay-delivery-service", "",new Bill(1000, "WAFA1234")
                , "pizza-shop");
        return LocalTransactionStepDefinition.builder()
                .in(onNextEvent)
                .blow(Stream.of(new BoxDelivredEvent()))
                .meta(this.meta)
                .send(Stream.of(cmd))
                .build();
    }

    private Optional<Supplier> deliveringBoxSupplier(){
        log.info("Hey, I'll start delevring your Box right now! ");

        Supplier s = () -> {
            log.info("Mchit n9aleb 3la Box");
            log.info("Safi l9it Box f store");
            log.info("Hana ghadi nediha");
            log.info("Safi wesaltha");
            return null;
        };
        return Optional.of(s);
    }

	@Override
	@RollBackBusinessLocalTransactional
	public LocalTransactionStepDefinition onError(ErrorLocalOccured errorLocalOccured) {
		return LocalTransactionStepDefinition.builder().in(errorLocalOccured).meta(this.meta).build();
	}
}
