package io.kermoss.trx.app.visitors.globaltx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.visitors.VisitorProvision;
import io.kermoss.trx.domain.GlobalTransaction;
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class InnerGlobalTxStepVisitorTest {

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

    private InnerGlobalTxStepVisitor innerGlobalTxStepVisitorUnderTest;

    @BeforeEach
    public void setUp() {
        //initMocks(this);
        innerGlobalTxStepVisitorUnderTest = spy(new InnerGlobalTxStepVisitor(mockBubbleCache, mockApplicationEventPublisher, mockProvision, mockCommandOrchestrator, mockUtilities));
    }

    @Test
    public void testVisitWhenMessageExist() {
        // Setup
        final GlobalTransactionStepDefinition transactionPipeline = mock(GlobalTransactionStepDefinition.class);
        final Supplier proccessSupplier = mock(Supplier.class);
        final BubbleMessage message = mock(BubbleMessage.class);
        final String FLTX = "FLTX";
        final String PGTX = "PGTX";

        when(transactionPipeline.getProcess()).thenReturn(Optional.of(proccessSupplier));
        when(mockUtilities.getBubleMessage(transactionPipeline)).thenReturn(Optional.of(message));
        when(message.getFLTX()).thenReturn(FLTX);
        when(message.getPGTX()).thenReturn(PGTX);
        doNothing().when(innerGlobalTxStepVisitorUnderTest).sendCommands(transactionPipeline, FLTX, PGTX);
        // Run the test
        innerGlobalTxStepVisitorUnderTest.visit(transactionPipeline);

        // Verify the results
        verify(innerGlobalTxStepVisitorUnderTest).sendCommands(transactionPipeline, FLTX, PGTX);
        verify(proccessSupplier).get();
    }

    @Test
    public void testVisitWhenMessageNotExist() {
        // Setup
        final GlobalTransactionStepDefinition transactionPipeline = mock(GlobalTransactionStepDefinition.class);
        final Supplier proccessSupplier = mock(Supplier.class);

        when(transactionPipeline.getProcess()).thenReturn(Optional.of(proccessSupplier));
        when(mockUtilities.getBubleMessage(transactionPipeline)).thenReturn(Optional.empty());
        doNothing().when(innerGlobalTxStepVisitorUnderTest).sendCommands(transactionPipeline, null, null);

        // Run the test
        innerGlobalTxStepVisitorUnderTest.visit(transactionPipeline);

        // Verify the results
        verify(innerGlobalTxStepVisitorUnderTest).sendCommands(transactionPipeline, null, null);
        verify(proccessSupplier).get();

    }

    @Test
    public void testSendCommands() {
        // Setup
        final GlobalTransactionStepDefinition transactionPipeline = mock(GlobalTransactionStepDefinition.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final BubbleMessage message = mock(BubbleMessage.class);
        final String gtx = "gtx";
        final String FLTX = "FLTX";
        final String PGTX = "PGTX";
        final BaseTransactionCommand send = mock(BaseTransactionCommand.class);
        final BaseTransactionEvent blow = mock(BaseTransactionEvent.class);
        final BaseTransactionCommand prepare = mock(BaseTransactionCommand.class);
        final Consumer attach = mock(Consumer.class);

        when(mockProvision.getGlobalTransaction()).thenReturn(globalTransaction);
        when(globalTransaction.getId()).thenReturn(gtx);
        doReturn(message).when(innerGlobalTxStepVisitorUnderTest).buildBubbleMessgae(globalTransaction, FLTX, PGTX);
        when(transactionPipeline.getSend()).thenReturn(Stream.of(send));
        when(transactionPipeline.getPrepare()).thenReturn(Stream.of(prepare));
        when(transactionPipeline.getBlow()).thenReturn(Stream.of(blow));
        when(transactionPipeline.getAttach()).thenReturn(Optional.of(attach));
        doNothing().when(innerGlobalTxStepVisitorUnderTest).receivePayLoad(transactionPipeline);
        doNothing().when(innerGlobalTxStepVisitorUnderTest).receivePayLoadGTX(transactionPipeline, gtx);
        when(send.getId()).thenReturn("send");
        when(prepare.getId()).thenReturn("prepare");
        when(blow.getId()).thenReturn("blow");

        // Run the test
        innerGlobalTxStepVisitorUnderTest.sendCommands(transactionPipeline, FLTX, PGTX);

        // Verify the results
        verify(mockBubbleCache).addBubble("send", message);
        verify(mockBubbleCache).addBubble("prepare", message);
        verify(mockBubbleCache).addBubble("blow", message);
        verify(mockCommandOrchestrator).receive(send);
        verify(mockCommandOrchestrator).prepare(prepare);
        verify(mockApplicationEventPublisher).publishEvent(blow);
        verify(attach).accept(gtx);
        verify(innerGlobalTxStepVisitorUnderTest).receivePayLoad(transactionPipeline);
        verify(innerGlobalTxStepVisitorUnderTest).receivePayLoadGTX(transactionPipeline, gtx);
    }

    @Test
    public void testBuildBubbleMessgae() {
        // Setup
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final String gtx = "gtx";
        final String traceId = "trace";
        final String FLTX = "FLTX";
        final String PGTX = "PGTX";

        when(globalTransaction.getId()).thenReturn(gtx);
        when(globalTransaction.getTraceId()).thenReturn(traceId);

        // Run the test
        final BubbleMessage result = innerGlobalTxStepVisitorUnderTest.buildBubbleMessgae(globalTransaction, FLTX, PGTX);

        // Verify the results
        assertEquals(result.getGLTX(), gtx);
        assertEquals(result.getTraceId(), traceId);
        assertEquals(result.getFLTX(), FLTX);
        assertEquals(result.getPGTX(), PGTX);
    }

    @Test
    public void testReceivePayLoad() {
        // Setup
        final GlobalTransactionStepDefinition transactionPipeline = mock(GlobalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        final Consumer target = mock(Consumer.class);
        final String payload = "payload";
        AbstractTransactionStepDefinition.ReceivedCommand command = new AbstractTransactionStepDefinition.ReceivedCommand(Object.class, target);

        when(transactionPipeline.getReceivedCommand()).thenReturn(command);
        when(transactionPipeline.getIn().getId()).thenReturn("someid");
        when(mockCommandOrchestrator.retreive(anyString(), any())).thenReturn(Optional.of(payload));

        // Run the test
        innerGlobalTxStepVisitorUnderTest.receivePayLoad(transactionPipeline);

        // Verify the results
        verify(mockCommandOrchestrator).retreive("someid", Object.class);
        verify(target).accept(any());
    }

    @Test
    public void testReceivePayLoadGTX() {
        // Setup
        final GlobalTransactionStepDefinition transactionPipeline = mock(GlobalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        final BiConsumer target = mock(BiConsumer.class);
        final String payload = "payload";
        final String gtx = "gtx";
        AbstractTransactionStepDefinition.ReceivedCommandGTX command = new AbstractTransactionStepDefinition.ReceivedCommandGTX(Object.class, target);

        when(transactionPipeline.getReceivedCommandGTX()).thenReturn(command);
        when(transactionPipeline.getIn().getId()).thenReturn("someid");
        when(mockCommandOrchestrator.retreive(anyString(), any())).thenReturn(Optional.of(payload));

        // Run the test
        innerGlobalTxStepVisitorUnderTest.receivePayLoadGTX(transactionPipeline, gtx);

        // Verify the results
        verify(mockCommandOrchestrator).retreive("someid", Object.class);
        verify(target).accept(same(gtx), any());
    }

    @Test
    public void testBuilder() {

        // Run the test
        final InnerGlobalTxStepVisitor result = InnerGlobalTxStepVisitor.builder()
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
