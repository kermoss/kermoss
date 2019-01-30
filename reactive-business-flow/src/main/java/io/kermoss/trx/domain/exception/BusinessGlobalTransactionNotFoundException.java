package io.kermoss.trx.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;

public class BusinessGlobalTransactionNotFoundException extends RuntimeException {
    Logger log = LoggerFactory.getLogger(BusinessGlobalTransactionInstableException.class);

    public BusinessGlobalTransactionNotFoundException(String message) {
        super(message);
    }

    public BusinessGlobalTransactionNotFoundException(AbstractTransactionStepDefinition pipeline) {
        log.error("Event {} throws a BusinessGlobalTransactionInstableException", pipeline.getIn().getClass().getName());
    }
}

