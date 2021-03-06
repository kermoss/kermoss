package io.kermoss.cmd.domain.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.kermoss.cmd.domain.InboundCommand;

@ExtendWith(MockitoExtension.class)
public class CommandRepositoryTest {
   
   @Spy
   private CommandRepositoryFake commandRepositoryUnderTest;

   @Test
   public void testFindOutboundCommand() {
	   commandRepositoryUnderTest.findOutboundCommand("Asxcf23");
	   verify(commandRepositoryUnderTest).findOutboundCommand(anyString());
  }
   
   @Test
   public void testFindByLTX() {
	   commandRepositoryUnderTest.findByLTX("Asxcf23","dffff");
	   verify(commandRepositoryUnderTest).findByLTX(anyString(),anyString());
  }
   
   @Test
   public void testFindByFLTXAndSubject() {
	   commandRepositoryUnderTest.findByFLTXAndSubject("Asxcf23","dffff");
	   verify(commandRepositoryUnderTest).findByFLTXAndSubject(anyString(),anyString());
  }
   
   
   @Test
   public void testFindOutboundCommandOpt() {
	   commandRepositoryUnderTest.findOutboundCommandOpt("Asxcf23");
	   verify(commandRepositoryUnderTest).findOutboundCommandOpt(anyString());
  }
   
   @Test
   public void testFindInboundCommandOpt() {
	   commandRepositoryUnderTest.findInboundommandOpt("Asxcf23");
	   verify(commandRepositoryUnderTest).findInboundommandOpt(anyString());
  }
   
   @Test
   public void testFindOneOpt() {
	   commandRepositoryUnderTest.findOneOpt("Asxcf23");
	   verify(commandRepositoryUnderTest).findOneOpt(anyString());
  }
   
   @Test
   public void testFindByRefId() {
	   commandRepositoryUnderTest.findByRefId("Asxcf23");
	   verify(commandRepositoryUnderTest).findByRefId(anyString());
  } 
   
   @Test
   public void testFindByIdAndStatus() {
	   commandRepositoryUnderTest.findByIdAndStatus("Asxcf23",InboundCommand.Status.COMPLETED);
	   verify(commandRepositoryUnderTest).findByIdAndStatus(anyString(),any(InboundCommand.Status.class));
  }
   @Test
   public void testFailedInboundCommands() {
	   commandRepositoryUnderTest.failedInboundCommands();
	   verify(commandRepositoryUnderTest).failedInboundCommands();
  } 
    
}