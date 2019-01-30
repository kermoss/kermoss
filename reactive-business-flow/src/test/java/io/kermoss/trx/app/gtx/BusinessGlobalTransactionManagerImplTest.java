package io.kermoss.trx.app.gtx;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.app.gtx.BusinessGlobalTransactionManagerImpl;
import io.kermoss.trx.app.gtx.BusinessGlobalTransactionService;
import io.kermoss.trx.app.gtx.GlobalTransactionMapper;
import io.kermoss.trx.app.gtx.RequestGlobalTransaction;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionNotFoundException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;

import java.util.Optional;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class BusinessGlobalTransactionManagerImplTest {

    @Mock
    private GlobalTransactionRepository mockGlobalTransactionRepository;
    @Mock
    private CommandOrchestrator mockCommandOrchestrator;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;
    @Mock
    private BubbleCache mockBubbleCache;
    @Mock
    private BusinessGlobalTransactionService mockBusinessGlobalTransactionService;
    @Mock
    private GlobalTransactionMapper mockGlobalTransactionMapper;
    @Mock
    private TransactionUtilities utilities;

    private BusinessGlobalTransactionManagerImpl businessGlobalTransactionManagerImplUnderTest;
    private GlobalTransactionStepDefinition pipeline;
    private RequestGlobalTransaction requestGlobalTransaction;
    private GlobalTransaction globalTransaction;


    @Before
    public void setUp() {
        initMocks(this);
        businessGlobalTransactionManagerImplUnderTest = new BusinessGlobalTransactionManagerImpl(mockGlobalTransactionRepository, mockCommandOrchestrator, mockApplicationEventPublisher, mockBubbleCache, mockBusinessGlobalTransactionService, mockGlobalTransactionMapper, utilities);

        pipeline = mock(GlobalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        requestGlobalTransaction =  mock(RequestGlobalTransaction.class);
        globalTransaction = mock(GlobalTransaction.class);

        when(mockGlobalTransactionMapper.mapTo(pipeline)).thenReturn(Optional.of(requestGlobalTransaction));
        when(mockBusinessGlobalTransactionService.participateToGlobalTransaction(any(Optional.class))).thenReturn(globalTransaction);
    }

    @Test
    public void testBeginWhenNewGlobalTx() {
        // Setup
        when(globalTransaction.isNew()).thenReturn(true);

        // Run the test
        businessGlobalTransactionManagerImplUnderTest.begin(pipeline);

        // Verify the results
        verify(mockGlobalTransactionRepository).save(same(globalTransaction));
        verify(pipeline).accept(any());

    }

    @Test
    public void testBeginWhenGlobalTxNotNew() {
        // Setup
        when(globalTransaction.isNew()).thenReturn(false);

        // Run the test
        businessGlobalTransactionManagerImplUnderTest.begin(pipeline);

        // Verify the results
        verify(mockGlobalTransactionRepository).save(same(globalTransaction));
        verify(pipeline, never()).accept(any());

    }

    @Test
    public void testCommitWhenGlobalTxStarted() {
        // Setup
        when(mockBusinessGlobalTransactionService.retrieveGlobalTransaction(Optional.of(requestGlobalTransaction))).thenReturn(Optional.of(globalTransaction));
        when(globalTransaction.getStatus()).thenReturn(GlobalTransaction.GlobalTransactionStatus.STARTED);

        // Run the test
        businessGlobalTransactionManagerImplUnderTest.commit(pipeline);

        // Verify the results
        verify(globalTransaction).setStatus(same(GlobalTransaction.GlobalTransactionStatus.COMITTED));
        verify(mockGlobalTransactionRepository).save(same(globalTransaction));
        verify(pipeline).accept(any());

    }

    @Test
    public void testCommitWhenGlobalTxNotStarted() {
        // Setup
        when(mockBusinessGlobalTransactionService.retrieveGlobalTransaction(Optional.of(requestGlobalTransaction))).thenReturn(Optional.of(globalTransaction));
        when(globalTransaction.getStatus()).thenReturn(GlobalTransaction.GlobalTransactionStatus.COMITTED);

        // Run the test
        businessGlobalTransactionManagerImplUnderTest.commit(pipeline);

        // Verify the results
        verify(globalTransaction, never()).setStatus(same(GlobalTransaction.GlobalTransactionStatus.COMITTED));
        verify(mockGlobalTransactionRepository, never()).save(same(globalTransaction));
        verify(pipeline).accept(any());

    }

    @Test(expected = BusinessGlobalTransactionNotFoundException.class)
    public void testCommitWhenGlobalTxNotFound() {
        // Setup
        when(mockBusinessGlobalTransactionService.retrieveGlobalTransaction(Optional.of(requestGlobalTransaction))).thenReturn(Optional.empty());

        // Run the test
        businessGlobalTransactionManagerImplUnderTest.commit(pipeline);

        // Verify the results
        verify(globalTransaction, never()).setStatus(any());
        verify(mockGlobalTransactionRepository, never()).save(any(GlobalTransaction.class));
        verify(pipeline, never()).accept(any());

    }
}
