package io.kermoss.trx.app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.app.aspect.BusinessLocalTransactionAspect;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.ltx.BusinessLocalTransactionManager;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class BusinessLocalTransactionAspectTest {

    @Mock
    private BusinessLocalTransactionManager mockBusinessLocalTransactionManager;

    private BusinessLocalTransactionAspect businessLocalTransactionAspectUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        businessLocalTransactionAspectUnderTest = new BusinessLocalTransactionAspect(mockBusinessLocalTransactionManager);
    }

    @Test(expected = PoincutDefinitionException.class)
    public void testLocalTransactionPointcut() {
        // Setup

        // Run the test
        businessLocalTransactionAspectUnderTest.localTransactionPointcut();

        // Verify the results
    }

    @Test(expected = PoincutDefinitionException.class)
    public void testMoveLocalTransactionPointcut() {
        // Setup

        // Run the test
        businessLocalTransactionAspectUnderTest.moveLocalTransactionPointcut();

        // Verify the results
    }
    @Test(expected = PoincutDefinitionException.class)
    public void testRollBackLocalTransactionPointcut() {
    	businessLocalTransactionAspectUnderTest.rollBackLocalTransactionPointcut();
    }

    @Test
    public void testBeginLocalTransaction() throws Throwable {
        // Setup
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);

        when(pjp.proceed()).thenReturn(localTransactionStepDefinition);

        // Run the test
        businessLocalTransactionAspectUnderTest.beginLocalTransaction(pjp);

        // Verify the results
        verify(mockBusinessLocalTransactionManager, times(1)).begin(localTransactionStepDefinition);

    }

    @Test
    public void testMoveLocalTransaction() throws Throwable {
        // Setup
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);

        when(pjp.proceed()).thenReturn(localTransactionStepDefinition);

        // Run the test
        businessLocalTransactionAspectUnderTest.moveLocalTransaction(pjp);

        // Verify the results
        verify(mockBusinessLocalTransactionManager, times(1)).commit(localTransactionStepDefinition);

    }
    
    
    @Test
    public void testRollBackTransaction() throws Throwable {
        // Setup
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final LocalTransactionStepDefinition localTransactionStepDefinition = mock(LocalTransactionStepDefinition.class);

        when(pjp.proceed()).thenReturn(localTransactionStepDefinition);

        // Run the test
        businessLocalTransactionAspectUnderTest.rollBackTransaction(pjp);

        // Verify the results
        verify(mockBusinessLocalTransactionManager, times(1)).rollBack(localTransactionStepDefinition);

    }
}
