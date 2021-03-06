package io.kermoss.trx.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class TransactionUtilitiesTest {

	@Mock
	private BubbleCache mockBubbleCache;

	private TransactionUtilities transactionUtilitiesUnderTest;

	@BeforeEach
	public void setUp() {
		transactionUtilitiesUnderTest = new TransactionUtilities(mockBubbleCache);
	}

	@Test
	public void testGetBubleMessageGlobalPipelineWhenEventRegistred() {
		// Setup
		final GlobalTransactionStepDefinition pipeline = mock(GlobalTransactionStepDefinition.class);
		final BaseTransactionEvent event = mock(BaseGlobalTransactionEvent.class);
		final String eventId = "eventid";
		final BubbleMessage message = mock(BubbleMessage.class);

		when(pipeline.getIn()).thenReturn(event);
		when(event.getId()).thenReturn(eventId);
		when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.of(message));

		// Run the test
		final Optional<BubbleMessage> result = transactionUtilitiesUnderTest.getBubleMessage(pipeline);

		// Verify the results
		assertEquals(Optional.of(message), result);
	}

	@Test
	public void testGetBubleMessageGlobalPipelineWhenEventNotRegistred() {
		// Setup
		final GlobalTransactionStepDefinition pipeline = mock(GlobalTransactionStepDefinition.class);
		final BaseTransactionEvent event = mock(BaseGlobalTransactionEvent.class);
		final String eventId = "eventid";

		when(pipeline.getIn()).thenReturn(event);
		when(event.getId()).thenReturn(eventId);
		when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.empty());

		// Run the test
		final Optional<BubbleMessage> result = transactionUtilitiesUnderTest.getBubleMessage(pipeline);

		// Verify the results
		assertEquals(Optional.empty(), result);
	}

	@Test
	public void testGetBubleMessageLocalPipelineWhenEventRegistred() {
		// Setup
		final LocalTransactionStepDefinition pipeline = mock(LocalTransactionStepDefinition.class);
		final BaseTransactionEvent event = mock(BaseLocalTransactionEvent.class);
		final String eventId = "eventid";
		final BubbleMessage message = mock(BubbleMessage.class);

		when(pipeline.getIn()).thenReturn(event);
		when(event.getId()).thenReturn(eventId);
		when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.of(message));

		// Run the test
		final Optional<BubbleMessage> result = transactionUtilitiesUnderTest.getBubleMessage(pipeline);

		// Verify the results
		assertEquals(Optional.of(message), result);
	}

	@Test
	public void testGetBubleMessageLocalPipelineWhenEventNotRegistred() {
		// Setup
		final LocalTransactionStepDefinition pipeline = mock(LocalTransactionStepDefinition.class);
		final BaseTransactionEvent event = mock(BaseLocalTransactionEvent.class);
		final String eventId = "eventid";

		when(pipeline.getIn()).thenReturn(event);
		when(event.getId()).thenReturn(eventId);
		when(mockBubbleCache.getBubble(eventId)).thenReturn(Optional.empty());

		// Run the test
		final Optional<BubbleMessage> result = transactionUtilitiesUnderTest.getBubleMessage(pipeline);

		// Verify the results
		assertEquals(Optional.empty(), result);
	}
}
