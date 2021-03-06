package io.kermoss.trx.app.visitors.localtx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.domain.LocalTransaction;
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OuterLocalTxStepVisitorTest {

    @Mock
    private BubbleCache mockBubbleCache;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;
    @Mock
    private VisitorProvision mockProvision;
    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    @Mock
    private TransactionUtilities mockUtilities;

    private OuterLocalTxStepVisitor outerLocalTxStepVisitorUnderTest;

    @BeforeEach
    public void setUp() {
        //initMocks(this);
        outerLocalTxStepVisitorUnderTest = new OuterLocalTxStepVisitor(mockBubbleCache, mockApplicationEventPublisher, mockProvision, mockCommandOrchestrator, mockUtilities);
    }

    @Test
    public void testVisit() {
        // Setup
        final LocalTransactionStepDefinition transactionPipeline = mock(LocalTransactionStepDefinition.class);
        final BaseTransactionEvent blow = mock(BaseTransactionEvent.class);
        final LocalTransaction transaction = mock(LocalTransaction.class, RETURNS_DEEP_STUBS);


        when(transactionPipeline.getBlow()).thenReturn(Stream.of(blow));
        when(blow.getId()).thenReturn("blow");
        when(mockProvision.getLocalTransaction()).thenReturn(transaction);

        // Run the test
        outerLocalTxStepVisitorUnderTest.visit(transactionPipeline);

        // Verify the results
        verify(mockBubbleCache).addBubble(same("blow"), any(BubbleMessage.class));
        verify(mockApplicationEventPublisher).publishEvent(blow);
    }

    @Test
    public void testBuilder() {

        // Run the test
        final OuterLocalTxStepVisitor result = OuterLocalTxStepVisitor.builder()
                .bubbleCache(mockBubbleCache)
                .applicationEventPublisher(mockApplicationEventPublisher)
                .provision(mockProvision)
                .commandOrchestrator(mockCommandOrchestrator)
                .transactionUtilities(mockUtilities)
                .build();

        // Verify the results
        assertEquals(mockBubbleCache, result.getBubbleCache());
        assertEquals(mockApplicationEventPublisher, result.getApplicationEventPublisher());
        assertEquals(mockProvision, result.getProvision());
        assertEquals(mockCommandOrchestrator, result.getCommandOrchestrator());
        assertEquals(mockUtilities, result.getUtilities());
    }
}
