package io.kermoss.cmd.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandUnmarchelingException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(CommandUnmarchelingException.class);

    public CommandUnmarchelingException(String msg) {
        log.error("An error happened while trying to decode command: {}", msg);
    }
}
