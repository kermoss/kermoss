package io.kermoss.bfm.cmd;
public class RollBackGlobalCommand extends BaseTransactionCommand {

    public RollBackGlobalCommand(String subject, String header, String payload, String destination) {
        super(subject, header, payload, destination);
    }

    public RollBackGlobalCommand() {
    }
}
