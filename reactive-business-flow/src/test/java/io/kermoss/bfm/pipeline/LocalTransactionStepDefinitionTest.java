package io.kermoss.bfm.pipeline;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.kermoss.bfm.cmd.BaseTransactionCommand;
import io.kermoss.bfm.event.BaseLocalTransactionEvent;
import io.kermoss.bfm.event.BaseTransactionEvent;
import io.kermoss.bfm.pipeline.AbstractTransactionStepDefinition;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.trx.app.visitors.localtx.StepLocalTxVisitor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class LocalTransactionStepDefinitionTest {

    @Mock
    private BaseLocalTransactionEvent mockIn;
    @Mock
    private Stream<BaseTransactionCommand> mockSend;
    @Mock
    private Stream<BaseTransactionEvent> mockBlow;
    @Mock
    private WorkerMeta mockMeta;
    private Optional<Supplier> process;
    private AbstractTransactionStepDefinition.ReceivedCommand receivedCommand;
    private Optional<Consumer<String>> attach;
    private AbstractTransactionStepDefinition.ReceivedCommandGTX receivedCommandGTX;

    private LocalTransactionStepDefinition localTransactionStepDefinitionUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        process = null;
        receivedCommand = null;
        attach = null;
        receivedCommandGTX = null;
        localTransactionStepDefinitionUnderTest = new LocalTransactionStepDefinition<>(mockIn,null, process, mockSend, mockBlow, receivedCommand, attach, receivedCommandGTX, mockMeta,null);
    }

    @Test
    public void testAccept() {
        // Setup
        final StepLocalTxVisitor visitor = mock(StepLocalTxVisitor.class);

        // Run the test
        localTransactionStepDefinitionUnderTest.accept(visitor);

        // Verify the results
        verify(visitor).visit(any());
    }

    @Test
    public void testBuilder() {
        // Setup
        final BaseLocalTransactionEvent in = mock(BaseLocalTransactionEvent.class);
        final Supplier process = mock(Supplier.class);
        final BaseTransactionCommand send = mock(BaseTransactionCommand.class);
        final BaseTransactionEvent blow = mock(BaseTransactionEvent.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final Consumer attach = mock(Consumer.class);

        // Run the test
        final LocalTransactionStepDefinition result = LocalTransactionStepDefinition.builder()
                .in(in)
                .process(Optional.of(process))
                .send(Stream.of(send))
                .blow(Stream.of(blow))
                .meta(meta)
                .attach(attach)
                .build();

        // Verify the results
        assertEquals(in, result.getIn());
        assertEquals(process, result.getProcess().get());
        assertTrue(result.getSend().allMatch(s -> s.equals(send)));
        assertTrue(result.getBlow().allMatch(b -> b.equals(blow)));
        assertEquals(meta, result.getMeta());
        assertEquals(attach, result.getAttach().get());
    }
}
