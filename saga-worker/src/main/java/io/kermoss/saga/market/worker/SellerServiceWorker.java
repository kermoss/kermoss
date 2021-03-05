package io.kermoss.saga.market.worker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.GlobalTransactionWorker;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.saga.market.event.HangOffEvent;
import io.kermoss.saga.market.event.PhoneRingingEvent;
import io.kermoss.trx.app.annotation.BusinessGlobalTransactional;
import io.kermoss.trx.app.annotation.CommitBusinessGlobalTransactional;

@Component
@Profile({"single","market","test"})
public class SellerServiceWorker extends GlobalTransactionWorker<PhoneRingingEvent, HangOffEvent> {

    public SellerServiceWorker() {
        super(new WorkerMeta("SellerService"));
    }
    @Override
    @BusinessGlobalTransactional
    public  GlobalTransactionStepDefinition onStart(PhoneRingingEvent phoneRingingEvent) {
    	return GlobalTransactionStepDefinition.builder()
                .in(phoneRingingEvent)
                .process(Optional.of(() -> {
                    System.out.println("Your ingrendients are available!");
                    return null;
                })).blow(Stream.of(new HangOffEvent()))
                .meta(this.meta)
                .build();
    }

    @Override
    @CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(HangOffEvent onCompleteEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(onCompleteEvent)
                .meta(this.meta)
                .build();
    }
    
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
          SellerServiceWorker newInstance = new SellerServiceWorker();
          for (Method m : newInstance.getClass().getDeclaredMethods()) {
        	  
        	  System.out.println(m.isBridge()+m.getName()+"+++"+Modifier.toString(m.getModifiers()));
        	  
        	  if(m.getName().equals("onStart")) {
        		  if (!m.isAnnotationPresent(BusinessGlobalTransactional.class)) {
        			  System.out.println("cococo");
        		  }
        	  }
        	  if(m.getName().equals("onComplete")) {
        		  if (!m.isAnnotationPresent(CommitBusinessGlobalTransactional.class)) {
        			  System.out.println("cococo");
        		  }
        	  }
          }
	}
    
    
}
