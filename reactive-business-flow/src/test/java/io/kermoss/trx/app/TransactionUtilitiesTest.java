package io.kermoss.trx.app;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class TransactionUtilitiesTest {

    @Mock
    private BubbleCache mockBubbleCache;

    private TransactionUtilities transactionUtilitiesUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        transactionUtilitiesUnderTest = new TransactionUtilities(mockBubbleCache);
    }

    @Test
    public void testGetBubleMessageGlobalPipelineWhenEventRegistred() {
        // Setup
        final GlobalTransactionStepDefinition pipeline = mock(GlobalTransactionStepDefinition.class);
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
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
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
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
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
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
        final BaseTransactionEvent event = mock(BaseTransactionEvent.class);
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
