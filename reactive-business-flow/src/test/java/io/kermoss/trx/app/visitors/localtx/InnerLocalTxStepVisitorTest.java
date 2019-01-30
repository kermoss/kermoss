package io.kermoss.trx.app.visitors.localtx;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.app.visitors.localtx.InnerLocalTxStepVisitor;
import io.kermoss.trx.domain.LocalTransaction;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class InnerLocalTxStepVisitorTest {

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

    private InnerLocalTxStepVisitor innerLocalTxStepVisitorUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        innerLocalTxStepVisitorUnderTest = spy(new InnerLocalTxStepVisitor(mockBubbleCache, mockApplicationEventPublisher, mockProvision, mockCommandOrchestrator, mockUtilities));
    }

    @Test
    public void testVisit() {
        // Setup
        final LocalTransactionStepDefinition transactionPipeline = mock(LocalTransactionStepDefinition.class);
        final String gtx = "gtx";
        final Supplier proccessSupplier = mock(Supplier.class);
        final LocalTransaction transaction = mock(LocalTransaction.class, RETURNS_DEEP_STUBS);
        final Consumer attach = mock(Consumer.class);
        final BaseTransactionCommand send = mock(BaseTransactionCommand.class);


        when(mockProvision.getLocalTransaction()).thenReturn(transaction);
        when(transaction.getGlobalTransaction().getId()).thenReturn(gtx);
        when(transactionPipeline.getProcess()).thenReturn(Optional.of(proccessSupplier));
        when(send.getId()).thenReturn("send");
        when(transactionPipeline.getSend()).thenReturn(Stream.of(send));
        when(transactionPipeline.getAttach()).thenReturn(Optional.of(attach));
        doNothing().when(innerLocalTxStepVisitorUnderTest).receivePayLoad(transactionPipeline);
        doNothing().when(innerLocalTxStepVisitorUnderTest).receivePayLoadGTX(transactionPipeline, gtx);

        // Run the test
        innerLocalTxStepVisitorUnderTest.visit(transactionPipeline);

        // Verify the results
        verify(proccessSupplier).get();
        verify(mockBubbleCache).addBubble(same("send"), any(BubbleMessage.class));
        verify(mockCommandOrchestrator).receive(send);
        verify(attach).accept(gtx);
        verify(innerLocalTxStepVisitorUnderTest).receivePayLoad(transactionPipeline);
        verify(innerLocalTxStepVisitorUnderTest).receivePayLoadGTX(transactionPipeline, gtx);


    }

    @Test
    public void testReceivePayLoad() {
        // Setup
        final LocalTransactionStepDefinition transactionPipeline = mock(LocalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        final Consumer target = mock(Consumer.class);
        final String payload = "payload";
        AbstractTransactionStepDefinition.ReceivedCommand command = new AbstractTransactionStepDefinition.ReceivedCommand(Object.class, target);

        when(transactionPipeline.getReceivedCommand()).thenReturn(command);
        when(transactionPipeline.getIn().getId()).thenReturn("someid");
        when(mockCommandOrchestrator.retreive(anyString(), any())).thenReturn(Optional.of(payload));
        // Run the test
        innerLocalTxStepVisitorUnderTest.receivePayLoad(transactionPipeline);

        // Verify the results
        verify(mockCommandOrchestrator).retreive("someid", Object.class);
        verify(target).accept(any());
    }

    @Test
    public void testReceivePayLoadGTX() {
        // Setup
        final LocalTransactionStepDefinition transactionPipeline = mock(LocalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);;
        final String gtx = "gtx";
        final BiConsumer target = mock(BiConsumer.class);
        final String payload = "payload";
        AbstractTransactionStepDefinition.ReceivedCommandGTX command = new AbstractTransactionStepDefinition.ReceivedCommandGTX(Object.class, target);

        when(transactionPipeline.getReceivedCommandGTX()).thenReturn(command);
        when(transactionPipeline.getIn().getId()).thenReturn("someid");
        when(mockCommandOrchestrator.retreive(anyString(), any())).thenReturn(Optional.of(payload));

        // Run the test
        innerLocalTxStepVisitorUnderTest.receivePayLoadGTX(transactionPipeline, gtx);

        // Verify the results
        verify(mockCommandOrchestrator).retreive("someid", Object.class);
        verify(target).accept(same(gtx), any());
    }

    @Test
    public void testBuilder() {


        // Run the test
        final InnerLocalTxStepVisitor result = InnerLocalTxStepVisitor.builder()
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
