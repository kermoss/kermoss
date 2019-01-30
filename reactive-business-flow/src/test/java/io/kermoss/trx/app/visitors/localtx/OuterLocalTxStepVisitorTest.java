package io.kermoss.trx.app.visitors.localtx;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.app.visitors.localtx.OuterLocalTxStepVisitor;
import io.kermoss.trx.domain.LocalTransaction;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


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

    @Before
    public void setUp() {
        initMocks(this);
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
