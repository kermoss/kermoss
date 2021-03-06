package io.kermoss.trx.app.aspect;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import io.kermoss.bfm.pipeline.LocalTransactionStepDefinition;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.ltx.BusinessLocalTransactionManager;

public class BusinessLocalTransactionAspectTest {

    @Mock
    private BusinessLocalTransactionManager mockBusinessLocalTransactionManager;

    private BusinessLocalTransactionAspect businessLocalTransactionAspectUnderTest;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        businessLocalTransactionAspectUnderTest = new BusinessLocalTransactionAspect(mockBusinessLocalTransactionManager);
    }

    @Test
    public void testLocalTransactionPointcut() {
        // Setup

        // Run the test
    	assertThrows(PoincutDefinitionException.class, 
    			()->businessLocalTransactionAspectUnderTest.localTransactionPointcut());

        // Verify the results
    }

    @Test
    public void testMoveLocalTransactionPointcut() {
        // Setup

        // Run the test
    	assertThrows(PoincutDefinitionException.class, 
    			()->businessLocalTransactionAspectUnderTest.moveLocalTransactionPointcut());

        // Verify the results
    }
    @Test
    public void testRollBackLocalTransactionPointcut() {
    	assertThrows(PoincutDefinitionException.class, 
    			()->businessLocalTransactionAspectUnderTest.rollBackLocalTransactionPointcut());
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
