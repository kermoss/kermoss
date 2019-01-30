package io.kermoss.infra;

import org.junit.Before;
import org.junit.Test;

import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class BubbleCacheTest {

    private BubbleCache bubbleCacheUnderTest;

    @Before
    public void setUp() {
        bubbleCacheUnderTest = new BubbleCache();
    }

    @Test
    public void testAddBubble() {
        // Setup
        final String key = "key";
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);

        // Run the test
        bubbleCacheUnderTest.addBubble(key, bubbleMessage);

        // Verify the results
        assertEquals(bubbleCacheUnderTest.getBubble(key).get(), bubbleMessage);
    }

    @Test
    public void testGetBubbleWhenNotExist() {
        // Setup
        final String eventId = "eventId";
        // Run the test
        final Optional<BubbleMessage> result = bubbleCacheUnderTest.getBubble(eventId);

        // Verify the results
        assertFalse(result.isPresent());
    }
}
