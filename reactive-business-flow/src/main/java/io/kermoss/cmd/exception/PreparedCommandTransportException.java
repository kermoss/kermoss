package io.kermoss.cmd.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PreparedCommandTransportException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(PreparedCommandTransportException.class);

    public PreparedCommandTransportException(String commandId) {

        log.error("An error happened while trying to transport Prepared command {}.", commandId);
    }

}
