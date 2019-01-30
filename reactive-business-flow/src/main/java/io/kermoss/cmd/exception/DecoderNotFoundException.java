package io.kermoss.cmd.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kermoss.trx.domain.exception.BusinessGlobalTransactionInstableException;

public class DecoderNotFoundException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(DecoderNotFoundException.class);

    public DecoderNotFoundException(String subject) {
        log.error("Cannot find decoder for {}! Please make sure to add it to decoders registry.", subject);
    }
}
