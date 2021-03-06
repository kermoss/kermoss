package io.kermoss.trx.app.gtx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import io.kermoss.bfm.event.BaseGlobalTransactionEvent;
import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.cmd.domain.repository.CommandRepository;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class GlobalTransactionMapperTest {

    @Mock
    private CommandRepository mockCommandRepository;
    @Mock
    private TransactionUtilities mockTransactionUtilities;


    @InjectMocks
    private GlobalTransactionMapper globalTransactionMapperUnderTest;

    @BeforeEach
    public void setUp() {
        //initMocks(this);
    }

    @Test
    public void testMapToWhenBubbleMessageNotExist() {
        // Setup
        final BaseGlobalTransactionEvent event = mock(BaseGlobalTransactionEvent.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String txName = "txname";

        final GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> pipeline = GlobalTransactionStepDefinition.builder()
                .in(event)
                .meta(meta)
                .build();

        when(meta.getTransactionName()).thenReturn(txName);
        when(mockTransactionUtilities.getBubleMessage(pipeline)).thenReturn(Optional.empty());

        // Run the test
        final RequestGlobalTransaction result = globalTransactionMapperUnderTest.mapTo(pipeline).get();

        // Verify the results

        assertEquals(result.getName(), txName);
        assertEquals(result.getEventRequestor(), event);
        assertEquals(result.getGTX(), null);
        assertEquals(result.getParent(), null);
        assertEquals(result.getTraceId(), null);
        assertEquals(result.getCommandRequestor(), null);

    }

    @Test
    public void testMapToWhenBubbleMessageExistAndCommandIdNull() {
        // Setup
        final BaseGlobalTransactionEvent event = mock(BaseGlobalTransactionEvent.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);
        final String txName = "txname";
        final String gtx = "gtx";
        final String pgtx = "pgtx";
        final String traceId = "traceId";

        final GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> pipeline = GlobalTransactionStepDefinition.builder()
                .in(event)
                .meta(meta)
                .build();

        when(mockTransactionUtilities.getBubleMessage(pipeline)).thenReturn(Optional.of(bubbleMessage));
        when(bubbleMessage.getGLTX()).thenReturn(gtx);
        when(bubbleMessage.getPGTX()).thenReturn(pgtx);
        when(bubbleMessage.getTraceId()).thenReturn(traceId);
        when(bubbleMessage.getCommandId()).thenReturn(null);
        when(meta.getTransactionName()).thenReturn(txName);

        // Run the test
        final RequestGlobalTransaction result = globalTransactionMapperUnderTest.mapTo(pipeline).get();

        // Verify the results

        assertEquals(result.getName(), txName);
        assertEquals(result.getEventRequestor(), event);
        assertEquals(result.getGTX(), gtx);
        assertEquals(result.getParent(), pgtx);
        assertEquals(result.getTraceId(), traceId);
        assertEquals(result.getCommandRequestor(), null);
    }

    @Test
    public void testMapToWhenBubbleMessageExistAndCommandIdNotNull() {
        // Setup
        final BaseGlobalTransactionEvent event = mock(BaseGlobalTransactionEvent.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);
        final InboundCommand command = mock(InboundCommand.class);
        final String txName = "txname";
        final String gtx = "gtx";
        final String pgtx = "pgtx";
        final String traceId = "traceId";
        final String commandId = "commandId";

        final GlobalTransactionStepDefinition<BaseGlobalTransactionEvent> pipeline = GlobalTransactionStepDefinition.builder()
                .in(event)
                .meta(meta)
                .build();

        when(mockTransactionUtilities.getBubleMessage(pipeline)).thenReturn(Optional.of(bubbleMessage));
        when(bubbleMessage.getGLTX()).thenReturn(gtx);
        when(bubbleMessage.getPGTX()).thenReturn(pgtx);
        when(bubbleMessage.getTraceId()).thenReturn(traceId);
        when(bubbleMessage.getCommandId()).thenReturn(commandId);
        when(meta.getTransactionName()).thenReturn(txName);
        when(mockCommandRepository.findOne(commandId)).thenReturn(command);

        // Run the test
        final RequestGlobalTransaction result = globalTransactionMapperUnderTest.mapTo(pipeline).get();

        // Verify the results

        assertEquals(result.getName(), txName);
        assertEquals(result.getEventRequestor(), event);
        assertEquals(result.getGTX(), gtx);
        assertEquals(result.getParent(), pgtx);
        assertEquals(result.getTraceId(), traceId);
        assertEquals(result.getCommandRequestor(), command);
    }
}
