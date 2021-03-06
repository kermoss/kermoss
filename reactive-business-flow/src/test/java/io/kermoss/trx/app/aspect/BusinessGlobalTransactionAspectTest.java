package io.kermoss.trx.app.aspect;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.bfm.pipeline.GlobalTransactionStepDefinition;
import io.kermoss.trx.app.exception.PoincutDefinitionException;
import io.kermoss.trx.app.gtx.BusinessGlobalTransactionManager;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class BusinessGlobalTransactionAspectTest {

    @Mock
    private BusinessGlobalTransactionManager mockBusinessTransactionManager;
    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    private BusinessGlobalTransactionAspect businessGlobalTransactionAspectUnderTest;

    @BeforeEach
    public void setUp() {
        businessGlobalTransactionAspectUnderTest = new BusinessGlobalTransactionAspect(mockBusinessTransactionManager, mockApplicationEventPublisher);
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
