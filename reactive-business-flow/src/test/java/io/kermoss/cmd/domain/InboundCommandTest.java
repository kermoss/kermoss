package io.kermoss.cmd.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.kermoss.cmd.domain.InboundCommand.InboundCommandBuilder;
import io.kermoss.cmd.domain.InboundCommand.Status;

@ExtendWith(MockitoExtension.class)
public class InboundCommandTest {

	@Spy
	private InboundCommand inboundCommandUnderTest;

	@Test
	public void testPublicConstructor() {
		new InboundCommand();
	}

	@Test
	public void testPublicConstructorWithVars() {
		new InboundCommand("s", "a", "a", "a", "a", "a", "a", "a", "as", Status.STARTED, "aaa");
	}

	@Test
	public void testGetStatus() {
		Status status = inboundCommandUnderTest.getStatus();
		verify(inboundCommandUnderTest).getStatus();
	}

	@Test
	public void testChangeStatusWithComplete() {
		inboundCommandUnderTest.changeStatus(Status.COMPLETED);
		verify(inboundCommandUnderTest).changeStatus(any(Status.class));
		assertThat(inboundCommandUnderTest.getCompletedTimestamp()).isNotNull();
	}

	@Test
	public void testChangeStatusWithFailed() {
		inboundCommandUnderTest.changeStatus(Status.FAILED);
		verify(inboundCommandUnderTest).changeStatus(any(Status.class));
		assertThat(inboundCommandUnderTest.getFailedTimestamp()).isNotNull();
	}

	@Test
	public void testChangeStatusToComplete() {
		inboundCommandUnderTest.changeStatusToCompleted();
		verify(inboundCommandUnderTest).changeStatusToCompleted();
		assertThat(inboundCommandUnderTest.getStatus()).isEqualTo(Status.COMPLETED);
	}

	@Test
	public void testChangeStatusToFailed() {
		inboundCommandUnderTest.changeStatusToFailed();
		verify(inboundCommandUnderTest).changeStatusToFailed();
		assertThat(inboundCommandUnderTest.getStatus()).isEqualTo(Status.FAILED);
	}
	
	@Test
	public void testRefID() {
		String refId = "Xsr3";
		inboundCommandUnderTest.setRefId(refId);
		assertThat(inboundCommandUnderTest.getRefId()).isEqualTo(refId);
	}

	@Test
	public void testBuilder(){
		InboundCommandBuilder inboundCommandBuilder = InboundCommand.builder();
		InboundCommand inboundCommand = inboundCommandBuilder.trace("qq").additionalHeaders("ss").
		destination("sdf").fLTX("sdf").gTX("qsd").lTX("sxc").payload("zzz").PGTX("qswx").
		refId("sdf").
		status(Status.COMPLETED).
		source("ss").subject("asu").
		build();
		inboundCommandBuilder.toString();
		inboundCommand.toString();
	}

}