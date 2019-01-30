package io.kermoss.saga.pizzashop.cmd;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.saga.common.contract.Need;

public class CallMarketCommand extends BaseTransactionCommand {

    public CallMarketCommand(String subject, String header, Need payload, String destination) {
        super(subject, header, payload, destination);
    }

    public CallMarketCommand() {
    }
}
