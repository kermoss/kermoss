package io.kermoss.cmd.domain.repository;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.OutboundCommand;

@RunWith(MockitoJUnitRunner.class)
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