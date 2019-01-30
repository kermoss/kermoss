package io.kermoss.cmd.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoderException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(DecoderException.class);

    public DecoderException(String msg) {
        log.error("An error happened while trying to decode command: {}", msg);
    }
}
