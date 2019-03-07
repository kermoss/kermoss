package io.kermoss.trx.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.trx.domain.GlobalTransaction.GlobalTransactionBuilder;
import io.kermoss.trx.domain.GlobalTransaction.GlobalTransactionStatus;

@RunWith(MockitoJUnitRunner.class)
public class GlobalTransactionTest {
    @Spy
	private GlobalTransaction globalTransactionUnderTest; 

    
    @Test
    public void testConstructor(){
    	new GlobalTransaction(UUID.randomUUID().toString(),System.currentTimeMillis(),
    			new HashSet<>(), GlobalTransactionStatus.COMITTED, new ArrayList<>());
    }
    
    @Test
    public void testBuilder(){
    
      GlobalTransaction globalTransaction = GlobalTransaction.builder().id(UUID.randomUUID().toString()).localTransactions(new ArrayList<>())
      .name("GlobalTransactionName").status(GlobalTransactionStatus.COMITTED).timestamp(System.currentTimeMillis()).
      variables(new HashSet<>()).build();

    }
     
    @Test
    public void testCreate(){
    
              GlobalTransaction globalTransaction = GlobalTransaction.create("GlobalTransactionName", "ASDEF");
                   
              assertThat(globalTransaction.isNew()).isTrue();
              assertThat(globalTransaction.getName()).isEqualTo("GlobalTransactionName");
              assertThat(globalTransaction.getTraceId()).isEqualTo("ASDEF");
     }
    
    @Test
    public void testAddLocalTransaction() {
    	
    	 globalTransactionUnderTest.addLocalTransaction(new LocalTransaction());
         assertThat(globalTransactionUnderTest.getLocalTransactions()).hasSize(1);
    }
    
    @Test
    public void testAddVariable() {
    	
    	 globalTransactionUnderTest.addVariable("itemName", "pizza");
         assertThat(globalTransactionUnderTest.getVariables()).hasSize(1);
    }
    @Test
    public void testAddGlobalTransactionVariable() {
    	 globalTransactionUnderTest.addVariable(new GlobalTransactionVariable());
         assertThat(globalTransactionUnderTest.getVariables()).hasSize(1);
    }
    @Test
    public void  testGetVariableValue() {
    	globalTransactionUnderTest.addVariable("itemName", "pizza");
    	assertThat(globalTransactionUnderTest.getVariableValue("itemName")).isEqualTo(Optional.of("pizza"));
    }

}