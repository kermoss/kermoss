package io.kermoss.trx.app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kermoss.cmd.exception.CommandNotFoundException;

public class PoincutDefinitionException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(CommandNotFoundException.class);

    public PoincutDefinitionException() {
        log.error("PointCut should not be called!");
    }

}
