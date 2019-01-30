package io.kermoss.trx.app.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.trx.app.aspect.BusinessGlobalTransactionAspect;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.gtx.BusinessGlobalTransactionManager;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;


public class BusinessGlobalTransactionAspectTest {

    @Mock
    private BusinessGlobalTransactionManager mockBusinessTransactionManager;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    private BusinessGlobalTransactionAspect businessGlobalTransactionAspectUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        businessGlobalTransactionAspectUnderTest = new BusinessGlobalTransactionAspect(mockBusinessTransactionManager, mockApplicationEventPublisher);
    }

    @Test(expected = PoincutDefinitionException.class)
    public void testGlobalTransactionPointcut() {
        // Setup

        // Run the test
        businessGlobalTransactionAspectUnderTest.globalTransactionPointcut();

        // Verify the results
    }

    @Test(expected = PoincutDefinitionException.class)
    public void testCommitLocalTransactionPointcut() {
        // Setup

        // Run the test
        businessGlobalTransactionAspectUnderTest.commitLocalTransactionPointcut();

        // Verify the results
    }

    @Test
    public void testBeginLocalTransaction() throws Throwable {
        // Setup
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final GlobalTransactionStepDefinition globalTransactionStepDefinition = mock(GlobalTransactionStepDefinition.class);

        when(pjp.proceed()).thenReturn(globalTransactionStepDefinition);

        // Run the test
        businessGlobalTransactionAspectUnderTest.beginLocalTransaction(pjp);

        // Verify the results
        verify(mockBusinessTransactionManager, times(1)).begin(globalTransactionStepDefinition);
    }

    @Test
    public void testMoveLocalTransaction() throws Throwable {
        // Setup
        final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        final GlobalTransactionStepDefinition globalTransactionStepDefinition = mock(GlobalTransactionStepDefinition.class);

        when(pjp.proceed()).thenReturn(globalTransactionStepDefinition);

        // Run the test
        businessGlobalTransactionAspectUnderTest.moveLocalTransaction(pjp);

        // Verify the results
        verify(mockBusinessTransactionManager, times(1)).commit(globalTransactionStepDefinition);

    }
}
