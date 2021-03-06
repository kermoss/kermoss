package io.kermoss.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class BubbleCacheTest {

    private BubbleCache bubbleCacheUnderTest;

    @BeforeEach
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
