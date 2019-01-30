package io.kermoss.saga.pizzashop.cmd;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.saga.common.contract.ToDilever;

public class OrderPizzaReadyForShipmentCommand extends BaseTransactionCommand {

    public OrderPizzaReadyForShipmentCommand(String subject, String header, ToDilever payload, String destination) {
        super(subject, header, payload, destination);
    }

    public OrderPizzaReadyForShipmentCommand() {
    }
}
