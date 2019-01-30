package io.kermoss.cmd.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandNotFoundException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(CommandNotFoundException.class);

    public CommandNotFoundException() {
        log.error("No Command was found in Database");
    }

}
