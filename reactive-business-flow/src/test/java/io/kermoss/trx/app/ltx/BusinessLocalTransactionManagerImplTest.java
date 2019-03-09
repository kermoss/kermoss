package io.kermoss.trx.app.ltx;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.event.ErrorLocalOccured;
import io.kermoss.bfm.event.ErrorOccured;
import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.bfm.worker.WorkerMeta;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.infra.BubbleMessage;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.ltx.BusinessLocalTransactionManagerImpl;
import io.kermoss.trx.app.visitors.localtx.StepLocalTxVisitor;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.LocalTransaction;
import io.kermoss.trx.domain.LocalTransaction.LocalTransactionStatus;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionNotFoundException;
import io.kermoss.trx.domain.exception.BusinessLocalTransactionNotFoundException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;
import wiremock.org.apache.commons.lang3.text.translate.NumericEntityUnescaper.OPTION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BusinessLocalTransactionManagerImplTest {

    @Mock
    private GlobalTransactionRepository mockGlobalTransactionRepository;
    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;
    @Mock
    private BubbleCache mockBubbleCache;
    @Mock
    private TransactionUtilities mockTxUtilities;

    private BusinessLocalTransactionManagerImpl businessLocalTransactionManagerImplUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        businessLocalTransactionManagerImplUnderTest = spy(new BusinessLocalTransactionManagerImpl(mockGlobalTransactionRepository, mockCommandOrchestrator, mockApplicationEventPublisher, mockBubbleCache, mockTxUtilities));
    }

    @Test
    public void testBeginWhenGlobalTxExistAndLocalTxNew() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final String gtx = "gtx";
        final String name = "ltx-name";
        ArgumentCaptor<LocalTransaction> transactionArgumentCaptor = ArgumentCaptor.forClass(LocalTransaction.class);

        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getTransactionName()).thenReturn(name);
        when(globalTransaction.getId()).thenReturn(gtx);
        doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(new ArrayList<>()).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(), anyString());
        when(mockTxUtilities.getBubleMessage(localTransactionStepDefinition)).thenReturn(Optional.empty());

        // Run the test
        businessLocalTransactionManagerImplUnderTest.begin(localTransactionStepDefinition);

        // Verify the results

        verify(globalTransaction).addLocalTransaction(transactionArgumentCaptor.capture());
        LocalTransaction localTransaction = transactionArgumentCaptor.getValue();
        assertEquals(localTransaction.getGlobalTransaction(), globalTransaction);
        verify(mockGlobalTransactionRepository).save(globalTransaction);
        verify(businessLocalTransactionManagerImplUnderTest ).updateLTX(localTransactionStepDefinition, localTransaction);
        verify(businessLocalTransactionManagerImplUnderTest).attachToParent(meta, gtx,null,localTransaction);
        verify(localTransactionStepDefinition, times(2)).accept(any());
    }

    @Test
    public void testBeginWhenGlobalTxExistAndLocalTxExist() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final String gtx = "gtx";
        final String name = "ltx-name";

        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getTransactionName()).thenReturn(name);
        when(globalTransaction.getId()).thenReturn(gtx);
        doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(Optional.of(localTransaction)).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(), anyString(),Optional.of(anyList()));

        // Run the test
        businessLocalTransactionManagerImplUnderTest.begin(localTransactionStepDefinition);

        // Verify the results

        verify(globalTransaction, never()).addLocalTransaction(any(LocalTransaction.class));
        verify(mockGlobalTransactionRepository, never()).save(any(GlobalTransaction.class));
        verify(businessLocalTransactionManagerImplUnderTest, never() ).updateLTX(any(LocalTransactionStepDefinition.class), any(LocalTransaction.class));
        verify(businessLocalTransactionManagerImplUnderTest, never()).attachToParent(any(WorkerMeta.class), anyString(),Optional.of(anyList()), any(LocalTransaction.class));
        verify(localTransactionStepDefinition, times(1)).accept(any());

    }

    @Test(expected = BusinessGlobalTransactionNotFoundException.class)
    public void testBeginWhenGlobalTxNotExist() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String name = "ltx-name";

        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getTransactionName()).thenReturn(name);
        doReturn(Optional.empty()).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);


        // Run the test
        businessLocalTransactionManagerImplUnderTest.begin(localTransactionStepDefinition);

        // Verify the results
        verify(mockGlobalTransactionRepository, never()).save(any(GlobalTransaction.class));

    }

    @Test
    public void testCommitWhenGlobalTxExistAndNestedCommitted() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final LocalTransaction nestedLocalTransaction = mock(LocalTransaction.class);
        final String gtx = "gtx";
        final String name = "ltx-name";

        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getTransactionName()).thenReturn(name);
        when(localTransaction.getNestedLocalTransactions()).thenReturn(new HashSet<>(Arrays.asList(nestedLocalTransaction)));
        when(localTransaction.getState()).thenReturn(LocalTransaction.LocalTransactionStatus.STARTED);
        when(nestedLocalTransaction.getState()).thenReturn(LocalTransaction.LocalTransactionStatus.COMITTED);
        doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(Optional.of(localTransaction)).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(), anyString(),Optional.of(anyList()));


        // Run the test
        businessLocalTransactionManagerImplUnderTest.commit(localTransactionStepDefinition);

        // Verify the results
        verify(localTransaction).setState(LocalTransaction.LocalTransactionStatus.COMITTED);
        verify(localTransactionStepDefinition, times(2)).accept(any());
    }

    @Test
    public void testCommitWhenGlobalTxExistAndNestedNotCommitted() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final LocalTransaction nestedLocalTransaction = mock(LocalTransaction.class);
        final String gtx = "gtx";
        final String name = "ltx-name";

        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getTransactionName()).thenReturn(name);
        when(localTransaction.getNestedLocalTransactions()).thenReturn(new HashSet<>(Arrays.asList(nestedLocalTransaction)));
        when(nestedLocalTransaction.getState()).thenReturn(LocalTransaction.LocalTransactionStatus.STARTED);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
        doReturn(Optional.of(localTransaction)).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(), anyString(),Optional.of(anyList()));

        // Run the test
        businessLocalTransactionManagerImplUnderTest.commit(localTransactionStepDefinition);

        // Verify the results
        verify(localTransaction, never()).setState(LocalTransaction.LocalTransactionStatus.COMITTED);
        verify(localTransactionStepDefinition, never()).accept(any());
    }

    @Test(expected = BusinessLocalTransactionNotFoundException.class)
    public void testCommitWhenNoGlobalTx() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);

        doReturn(Optional.empty()).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);

        // Run the test
        businessLocalTransactionManagerImplUnderTest.commit(localTransactionStepDefinition);

    }

    @Test
    public void testFindGlobalTransaction() {
        // Setup
        final String GTX = "GTX";
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessLocalTransactionManagerImplUnderTest.findGlobalTransaction(GTX);

        // Verify the results
        assertEquals(Optional.of(globalTransaction), result);
    }

    @Test
    public void testBuildInnerLocalTxStep() {
        // Setup
        final LocalTransaction localTransaction = mock(LocalTransaction.class);

        // Run the test
        final StepLocalTxVisitor result = businessLocalTransactionManagerImplUnderTest.buildInnerLocalTxStep(Optional.of(localTransaction));

        // Verify the results
        assertEquals(result.getProvision().getLocalTransaction(), localTransaction);
        assertEquals(result.getBubbleCache(), mockBubbleCache);
        assertEquals(result.getApplicationEventPublisher(), mockApplicationEventPublisher);
        assertEquals(result.getCommandOrchestrator(), mockCommandOrchestrator);
    }

    @Test
    public void testBuildOuterLocalTxStep() {
        // Setup
        final LocalTransaction localTransaction = mock(LocalTransaction.class);

        // Run the test
        final StepLocalTxVisitor result = businessLocalTransactionManagerImplUnderTest.buildOuterLocalTxStep(Optional.of(localTransaction));

        // Verify the results
        assertEquals(result.getProvision().getLocalTransaction(), localTransaction);
        assertEquals(result.getBubbleCache(), mockBubbleCache);
        assertEquals(result.getApplicationEventPublisher(), mockApplicationEventPublisher);
        assertEquals(result.getCommandOrchestrator(), mockCommandOrchestrator);
    }

    @Test
    public void testGetGlobalTransactionWhenBubbleMessageExistAndGtxNull() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final BubbleMessage message = mock(BubbleMessage.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String pgtx = "PGTX";
        final String gtx = "GTX";

        when(mockTxUtilities.getBubleMessage(localTransactionStepDefinition)).thenReturn(Optional.of(message));
        when(message.getPGTX()).thenReturn(pgtx);
        when(message.getGLTX()).thenReturn(null);
        when(localTransactionStepDefinition.getMeta()).thenReturn(meta);
        when(meta.getChildOf()).thenReturn(gtx);
        when(mockGlobalTransactionRepository.findByNameAndParent(gtx, pgtx)).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessLocalTransactionManagerImplUnderTest.getGlobalTransaction(localTransactionStepDefinition);

        // Verify the results
        verify(businessLocalTransactionManagerImplUnderTest, never()).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository).findByNameAndParent(anyString(), anyString());
        assertEquals(globalTransaction, result.get());

    }

    @Test
    public void testGetGlobalTransactionWhenBubbleMessageExistAndGtxNotNullAndPgtxNotNull() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final BubbleMessage message = mock(BubbleMessage.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String pgtx = "PGTX";
        final String gtx = "GTX";

        when(mockTxUtilities.getBubleMessage(localTransactionStepDefinition)).thenReturn(Optional.of(message));
        when(message.getPGTX()).thenReturn(pgtx);
        when(message.getGLTX()).thenReturn(gtx);
        when(mockGlobalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessLocalTransactionManagerImplUnderTest.getGlobalTransaction(localTransactionStepDefinition);

        // Verify the results
        verify(businessLocalTransactionManagerImplUnderTest).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository, never()).findByNameAndParent(anyString(), anyString());
        assertEquals(globalTransaction, result.get());

    }

    @Test
    public void testGetGlobalTransactionWhenBubbleMessageExistAndGtxNotNullAndPgtxNull() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
        final BubbleMessage message = mock(BubbleMessage.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String gtx = "GTX";

        when(mockTxUtilities.getBubleMessage(localTransactionStepDefinition)).thenReturn(Optional.of(message));
        when(message.getPGTX()).thenReturn(null);
        when(message.getGLTX()).thenReturn(gtx);
        when(mockGlobalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessLocalTransactionManagerImplUnderTest.getGlobalTransaction(localTransactionStepDefinition);

        // Verify the results
        verify(businessLocalTransactionManagerImplUnderTest).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository, never()).findByNameAndParent(anyString(), anyString());
        assertEquals(globalTransaction, result.get());

    }

    @Test(expected = BusinessGlobalTransactionNotFoundException.class)
    public void testGetGlobalTransactionWhenBubbleMessageNotExist() {
        // Setup
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);

        when(mockTxUtilities.getBubleMessage(localTransactionStepDefinition)).thenReturn(Optional.empty());

        // Run the test
        final Optional<GlobalTransaction> result = businessLocalTransactionManagerImplUnderTest.getGlobalTransaction(localTransactionStepDefinition);

        // Verify the results
        verify(businessLocalTransactionManagerImplUnderTest, never()).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository, never()).findByNameAndParent(anyString(), anyString());

    }

    @Test
    public void testAttachToParentWhenLocalTxExist() {
        // Setup
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String gtx = "gtx";
        final String childOf = "ltx-parent";
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final LocalTransaction nestTransaction = mock(LocalTransaction.class);

        when(meta.getChildOf()).thenReturn(childOf);
        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(Optional.of(localTransaction)).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(), anyString(),Optional.of(anyList()));


        // Run the test
        businessLocalTransactionManagerImplUnderTest.attachToParent(meta, gtx,null, nestTransaction);

        // Verify the results
        verify(localTransaction, times(1)).addNestedLocalTransaction(nestTransaction);
    }

    @Test
    public void testAttachToParentWhenLocalTxNotExist() {
        // Setup
        final WorkerMeta meta = mock(WorkerMeta.class);
        final String gtx = "gtx";
        final String childOf = "ltx-parent";
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final LocalTransaction nestTransaction = mock(LocalTransaction.class);

        when(meta.getChildOf()).thenReturn(childOf);
        when(mockGlobalTransactionRepository.findById(gtx)).thenReturn(Optional.of(mock(GlobalTransaction.class)));
        doReturn(new ArrayList<>()).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(gtx, childOf);
        // Run the test
        businessLocalTransactionManagerImplUnderTest.attachToParent(meta, gtx, null,nestTransaction);

        // Verify the results
        verify(localTransaction, never()).addNestedLocalTransaction(nestTransaction);
    }

    @Test
    public void testUpdateLTXWhenMessageExist() {
        // Setup
        final LocalTransactionStepDefinition pipeline = mock(LocalTransactionStepDefinition.class);
        final LocalTransaction ltx = mock(LocalTransaction.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);
        final String fltx = "fltx";

        when(mockTxUtilities.getBubleMessage(pipeline)).thenReturn(Optional.of(bubbleMessage));
        when(bubbleMessage.getFLTX()).thenReturn(fltx);

        // Run the test
        businessLocalTransactionManagerImplUnderTest.updateLTX(pipeline, ltx);

        // Verify the results
        verify(ltx).setFLTX(fltx);
    }

    @Test
    public void testUpdateLTXWhenMessageNotExist() {
        // Setup
        final LocalTransactionStepDefinition pipeline = mock(LocalTransactionStepDefinition.class);
        final LocalTransaction ltx = mock(LocalTransaction.class);
        final BubbleMessage bubbleMessage = mock(BubbleMessage.class);

        when(mockTxUtilities.getBubleMessage(pipeline)).thenReturn(Optional.empty());

        // Run the test
        businessLocalTransactionManagerImplUnderTest.updateLTX(pipeline, ltx);

        // Verify the results
        verify(ltx, never()).setFLTX(anyString());
    }

    @Test
    public void testGetLocalTransactionWhenGlobalTxExist() {
        // Setup
        final String gtx = "gtx";
        final String name = "name";
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        when(mockGlobalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));
        when(globalTransaction.getLocalTransactions()).thenReturn(Arrays.asList(localTransaction));
        when(localTransaction.getName()).thenReturn(name);

        // Run the test
        final Optional<LocalTransaction> result = businessLocalTransactionManagerImplUnderTest.getLocalTransaction(gtx, name,null);

        // Verify the results
        assertEquals(localTransaction, result.get());
    }

    @Test
    public void testGetLocalTransactionWhenGlobalTxExistAndLocalLtxDifferent() {
        // Setup
        final String gtx = "gtx";
        final String name = "name";
        final LocalTransaction localTransaction = mock(LocalTransaction.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        when(mockGlobalTransactionRepository.findById(gtx)).thenReturn(Optional.of(globalTransaction));
        when(globalTransaction.getLocalTransactions()).thenReturn(Arrays.asList(localTransaction));
        when(localTransaction.getName()).thenReturn("anothername");

        // Run the test
        final Optional<LocalTransaction> result = businessLocalTransactionManagerImplUnderTest.getLocalTransaction(gtx, name,null);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testGetLocalTransactionWhenGlobalTxNotExist() {

        when(mockGlobalTransactionRepository.findById(anyString())).thenReturn(Optional.empty());

        // Run the test
        final Optional<LocalTransaction> result = businessLocalTransactionManagerImplUnderTest.getLocalTransaction(anyString(), anyString(),any());

        // Verify the results
        assertEquals(Optional.empty(), result);
    }
    
    @Test
    public void testMarkAsRollbacked() {
    	final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
    	final LocalTransaction localTransaction = mock(LocalTransaction.class);
    	when(localTransaction.getState()).thenReturn(LocalTransactionStatus.COMITTED);
    	businessLocalTransactionManagerImplUnderTest.markAsRollbacked(localTransaction, localTransactionStepDefinition);
        
    	verify(localTransactionStepDefinition,times(2)).accept(any(StepLocalTxVisitor.class));
    
    }
    
    @Test
    public void testRollBackForErrorLocalOccured() {
    	final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
    	final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
    	when(localTransactionStepDefinition.getMeta()).thenReturn(new WorkerMeta("XXX"));
    	doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
    	LocalTransaction mock = mock(LocalTransaction.class);
		doNothing().when(businessLocalTransactionManagerImplUnderTest).markAsRollbacked(mock, localTransactionStepDefinition);
    	when(localTransactionStepDefinition.getIn()).thenReturn(new ErrorLocalOccured());
    	doReturn(new ArrayList<>()).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(),anyString());
    	businessLocalTransactionManagerImplUnderTest.rollBack(localTransactionStepDefinition);
    	
    }
    @Test
    public void testRollBack() {
    	final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
    	final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
    	when(localTransactionStepDefinition.getMeta()).thenReturn(new WorkerMeta("XXX"));
    	doReturn(Optional.of(globalTransaction)).when(businessLocalTransactionManagerImplUnderTest).getGlobalTransaction(localTransactionStepDefinition);
    	LocalTransaction localTransaction = mock(LocalTransaction.class);
		doNothing().when(businessLocalTransactionManagerImplUnderTest).markAsRollbacked(localTransaction, localTransactionStepDefinition);
    	when(localTransactionStepDefinition.getIn()).thenReturn(new ErrorOccured());
    	doReturn(Optional.of(localTransaction)).when(businessLocalTransactionManagerImplUnderTest).getLocalTransaction(anyString(),anyString(),Optional.ofNullable(any(ArrayList.class)));
    	businessLocalTransactionManagerImplUnderTest.rollBack(localTransactionStepDefinition);
    	
    }
    
    @Test
    public void testMarkAsRollbackedWithStateRollbacked() {
    	final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);
    	final LocalTransaction localTransaction = mock(LocalTransaction.class);
    	when(localTransaction.getState()).thenReturn(LocalTransactionStatus.ROLLBACKED);
    	businessLocalTransactionManagerImplUnderTest.markAsRollbacked(localTransaction, localTransactionStepDefinition);
        
    	verify(localTransactionStepDefinition,times(1)).accept(any(StepLocalTxVisitor.class));
    
    }
}
