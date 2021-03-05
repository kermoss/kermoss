package io.kermoss.trx.app.gtx;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.cmd.domain.InboundCommand;
import io.kermoss.trx.domain.GlobalTransaction;
import io.kermoss.trx.domain.exception.BusinessGlobalTransactionInstableException;
import io.kermoss.trx.domain.repository.GlobalTransactionRepository;



@RunWith(PowerMockRunner.class)
@PrepareForTest( GlobalTransaction.class )
public class BusinessGlobalTransactionServiceImplTest {

    @Mock
    GlobalTransactionRepository mockGlobalTransactionRepository;
    
    @Mock
    ApplicationEventPublisher mockPublisher;

    private BusinessGlobalTransactionServiceImpl businessGlobalTransactionServiceImplUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        businessGlobalTransactionServiceImplUnderTest = spy(new BusinessGlobalTransactionServiceImpl(mockGlobalTransactionRepository,null));
    }

    @Test
    public void testFindGlobalTransactionWhenGlobalTxExist() {
        // Setup
        final String id = "id";
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);
        Optional<GlobalTransaction> opt = Optional.of(globalTransaction);
		when(mockGlobalTransactionRepository.findById(id)).thenReturn(opt);
        
        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.findGlobalTransaction(id);

        // Verify the results
        assertEquals(Optional.of(globalTransaction), result);
    }
    @Test
    public void testFindGlobalTransactionWhenGlobalTxisNull() {
        // Setup
        final String id = "id";
		when(mockGlobalTransactionRepository.findById(id)).thenReturn(null);
        
        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.findGlobalTransaction(id);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testFindGlobalTransactionWhenGlobalTxNotExist() {
        // Setup
        final String id = "id";
		when(mockGlobalTransactionRepository.findById(id)).thenReturn(Optional.empty());
        
        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.findGlobalTransaction(id);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testParticipateToGlobalTransactionWhenFound() {
        // Setup
        final Optional<RequestGlobalTransaction> orgt = Optional.of(mock(RequestGlobalTransaction.class));
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);


        when(mockGlobalTransactionRepository.findByNameAndParentAndStatus(anyString(), anyString(), any())).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final GlobalTransaction result = businessGlobalTransactionServiceImplUnderTest.participateToGlobalTransaction(orgt);

        // Verify the results
        assertEquals(globalTransaction, result);
        verify(globalTransaction).setStatus(GlobalTransaction.GlobalTransactionStatus.STARTED);
        verify(businessGlobalTransactionServiceImplUnderTest, never()).startNewGlobalTransaction(any(RequestGlobalTransaction.class));
    }

    @Test
    public void testParticipateToGlobalTransactionWhenNotFound() {
        // Setup
        final RequestGlobalTransaction orgt = mock(RequestGlobalTransaction.class);


        when(mockGlobalTransactionRepository.findByNameAndParentAndStatus(anyString(), anyString(), any())).thenReturn(Optional.empty());

        // Run the test
        final GlobalTransaction result = businessGlobalTransactionServiceImplUnderTest.participateToGlobalTransaction(Optional.of(orgt));

        // Verify the results
        verify(businessGlobalTransactionServiceImplUnderTest).startNewGlobalTransaction(same(orgt));
    }

    @Test
    public void testParticipateToGlobalTransactionWhenRequestGlobalTransactionNotPresent() {
        // Setup
        final Optional<RequestGlobalTransaction> orgt = Optional.empty();

        // Run the test
        final GlobalTransaction result = businessGlobalTransactionServiceImplUnderTest.participateToGlobalTransaction(orgt);

        // Verify the results
        assertEquals(null, result);
    }

    @Test
    public void testRetrieveGlobalTransactionRequestGlobalTransactionNotPresent() {
        // Setup
        final Optional<RequestGlobalTransaction> orgt = Optional.empty();

        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.retrieveGlobalTransaction(orgt);

        // Verify the results
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testRetrieveGlobalTransactionGlobalTransactionHasParentAndIdNull() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        when(rgt.getParent()).thenReturn("parent");
        when(rgt.getGTX()).thenReturn(null);
        when(rgt.getName()).thenReturn("gtxname");
        when(mockGlobalTransactionRepository.findByNameAndParent("gtxname", "parent")).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.retrieveGlobalTransaction(Optional.of(rgt));

        // Verify the results
        assertEquals(globalTransaction, result.get());
        verify(businessGlobalTransactionServiceImplUnderTest, never()).findGlobalTransaction(anyString());
    }

    @Test
    public void testRetrieveGlobalTransactionGlobalTransactionHasParentAndId() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        when(rgt.getParent()).thenReturn("parent");
        when(rgt.getGTX()).thenReturn("id");
        Optional<GlobalTransaction> opt = Optional.of(globalTransaction);
		when(mockGlobalTransactionRepository.findById("id")).thenReturn(opt);

        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.retrieveGlobalTransaction(Optional.of(rgt));

        // Verify the results
        assertEquals(globalTransaction, result.get());
        verify(businessGlobalTransactionServiceImplUnderTest).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository, never()).findByNameAndParent(anyString(), anyString());
    }

    @Test
    public void testRetrieveGlobalTransactionGlobalTransactionHasNoParentAndId() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        when(rgt.getParent()).thenReturn(null);
        when(rgt.getGTX()).thenReturn("id");
        when(mockGlobalTransactionRepository.findById("id")).thenReturn(Optional.of(globalTransaction));

        // Run the test
        final Optional<GlobalTransaction> result = businessGlobalTransactionServiceImplUnderTest.retrieveGlobalTransaction(Optional.of(rgt));

        // Verify the results
        assertEquals(globalTransaction, result.get());
        verify(businessGlobalTransactionServiceImplUnderTest).findGlobalTransaction(anyString());
        verify(mockGlobalTransactionRepository, never()).findByNameAndParent(anyString(), anyString());
    }

    @Test(expected = BusinessGlobalTransactionInstableException.class)
    public void testStartGlobalTransactionWhenGTXNotNull() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);

        when(rgt.getGTX()).thenReturn(anyString());

        // Run the test
        businessGlobalTransactionServiceImplUnderTest.startNewGlobalTransaction(rgt);

    }

    @Test
    public void testStartGlobalTransactionWhenSynchronizedVote() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);
        final InboundCommand command = mock(InboundCommand.class);
        final String traceId = "traceId";
        final String name = "trxname";
        final String parent = "parent";
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        setupStartTrx(rgt, command, traceId, name, parent, globalTransaction);
        when(command.getStatus()).thenReturn(InboundCommand.Status.PREPARED);
        // Run the test
        GlobalTransaction result = businessGlobalTransactionServiceImplUnderTest.startNewGlobalTransaction(rgt);

        // Verify the results
        verify(globalTransaction).setStatus(same(GlobalTransaction.GlobalTransactionStatus.PREPARED));
        verify(globalTransaction).setParent(same(parent));
        assertEquals(result, globalTransaction);

    }

    @Test
    public void testStartGlobalTransactionWhenNotSynchronizedVote() {
        // Setup
        final RequestGlobalTransaction rgt = mock(RequestGlobalTransaction.class);
        final InboundCommand command = mock(InboundCommand.class);
        final String traceId = "traceId";
        final String name = "trxname";
        final String parent = "parent";
        final GlobalTransaction globalTransaction = mock(GlobalTransaction.class);

        setupStartTrx(rgt, command, traceId, name, parent, globalTransaction);
        when(command.getStatus()).thenReturn(InboundCommand.Status.STARTED);
        // Run the test
        GlobalTransaction result = businessGlobalTransactionServiceImplUnderTest.startNewGlobalTransaction(rgt);

        // Verify the results
        verify(globalTransaction).setStatus(same(GlobalTransaction.GlobalTransactionStatus.STARTED));
        verify(globalTransaction).setParent(same(parent));
        assertEquals(result, globalTransaction);


    }


    private void setupStartTrx(RequestGlobalTransaction rgt, InboundCommand command, String traceId, String name, String parent, GlobalTransaction globalTransaction) {
        mockStatic(GlobalTransaction.class);
        when(rgt.getGTX()).thenReturn(null);
        when(rgt.getCommandRequestor()).thenReturn(command);
        when(rgt.getParent()).thenReturn(parent);
        when(rgt.getName()).thenReturn(name);
        when(GlobalTransaction.create(name,null)).thenReturn(globalTransaction);
    }
}
