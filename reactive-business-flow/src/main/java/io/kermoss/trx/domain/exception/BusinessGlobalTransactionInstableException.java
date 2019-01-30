package io.kermoss.trx.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kermoss.trx.app.gtx.RequestGlobalTransaction;

public class BusinessGlobalTransactionInstableException extends RuntimeException {

    Logger log = LoggerFactory.getLogger(BusinessGlobalTransactionInstableException.class);

    public BusinessGlobalTransactionInstableException(String message) {
        super(message);
    }

    public BusinessGlobalTransactionInstableException(RequestGlobalTransaction rgt) {
        log.error("Event {} throws a BusinessGlobalTransactionInstableException", rgt.getClass().getName());
    }
}
