package io.kermoss.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Consumer;

@Component
public class KermossTxLogger {

    private static final Logger logger = LoggerFactory.getLogger(KermossTxLogger.class);

    public  String printJsonObject(Object o){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            logger.info("Error while trying to return Object Json");
        }
        return null;
    }


    public  void logTransactionState(Consumer c){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
            @Override
            public void suspend() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void beforeCommit(boolean b) {

            }

            @Override
            public void beforeCompletion() {

            }

            @Override
            public void afterCommit() {
            }

            @Override
            public void afterCompletion(int i) {
                String txStatus = null;
                switch (i) {
                    case 0:
                        txStatus = "COMMITTED";
                        break;
                    case 1:
                        txStatus = "ROLLED_BACK";
                        break;
                    case 2:
                        txStatus = "UNKNOWN";
                        break;
                }
                c.accept(txStatus);
            }
        });
    }

}
