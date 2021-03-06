package io.kermoss.trx.app.gtx;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.cmd.app.CommandOrchestrator;
import io.kermoss.infra.BubbleCache;
import io.kermoss.trx.app.TransactionUtilities;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
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


    @BeforeEach
    public void setUp() {
        businessGlobalTransactionManagerImplUnderTest = new BusinessGlobalTransactionManagerImpl(mockGlobalTransactionRepository, mockCommandOrchestrator, mockApplicationEventPublisher, mockBubbleCache, mockBusinessGlobalTransactionService, mockGlobalTransactionMapper, utilities);

        pipeline = mock(GlobalTransactionStepDefinition.class, RETURNS_DEEP_STUBS);
        requestGlobalTransaction =  mock(RequestGlobalTransaction.class);
        globalTransaction = mock(GlobalTransaction.class);

        when(mockGlobalTransactionMapper.mapTo(pipeline)).thenReturn(Optional.of(requestGlobalTransaction));
        
    }

    @Test
    public void testBeginWhenNewGlobalTx() {
        // Setup
    	when(mockBusinessGlobalTransactionService.participateToGlobalTransaction(any(Optional.class))).thenReturn(globalTransaction);
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
    	when(mockBusinessGlobalTransactionService.participateToGlobalTransaction(any(Optional.class))).thenReturn(globalTransaction);
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

    
}
