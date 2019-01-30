package io.kermoss.saga.pizzasship.cmd;


import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.saga.common.contract.Bill;

public class PayBillCommand extends BaseTransactionCommand {

    public PayBillCommand(String subject, String header, Bill payload, String destination) {
        super(subject, header, payload, destination);
    }

    public PayBillCommand() {
    }
}
