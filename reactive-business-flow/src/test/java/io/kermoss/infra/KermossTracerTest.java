package io.kermoss.infra;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationEventPublisher;

import io.kermoss.infra.GtxSpanStarted;
import io.kermoss.infra.KermossTracer;
import io.kermoss.trx.domain.GlobalTransaction;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

public class KermossTracerTest {

    @Mock
    private Tracer mockTracer;
    @Mock
    private ApplicationEventPublisher mockPublisher;

    @InjectMocks
    @Spy
    private KermossTracer kermossTracerUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testCloseSpanWhenEvent() {
        // Setup
        final GtxSpanStarted gtxSpanStarted = null;
        final Span span = mock(Span.class);
        doReturn(span).when(kermossTracerUnderTest).currentSpan();

        // Run the test
        kermossTracerUnderTest.closeSpan(gtxSpanStarted);

        // Verify the results
        verify(kermossTracerUnderTest).closeSpan(same(span));
    }

    @Test
    public void testCloseSpanEWhenEvent() {
        // Setup
        final GtxSpanStarted gtxSpanStarted = null;
        final Span span = mock(Span.class);
        doReturn(span).when(kermossTracerUnderTest).currentSpan();

        // Run the test
        kermossTracerUnderTest.closeSpanE(gtxSpanStarted);

        // Verify the results
        verify(kermossTracerUnderTest).closeSpan(same(span));
    }

    @Test
    public void testStartGtxSpanWhenSpanNotNullAndHasNoGtx() {
        // Setup
        final String name = "name";
        final String gtx = "gtx";
        final String traceId = "4e30f7340b3fb631";
        final Span span = mock(Span.class);
        final Span newGlobalSpan = mock(Span.class);

        when(mockTracer.createSpan(anyString(), any(Span.class))).thenReturn(newGlobalSpan);
        // Run the test
        final Span result = kermossTracerUnderTest.startGtxSpan(name, gtx, traceId, span);

        // Verify the results
        verify(newGlobalSpan, atLeastOnce()).tag(anyString(), anyString());
        verify(mockPublisher).publishEvent(any(GtxSpanStarted.class));
        assertEquals(result, newGlobalSpan);
    }

    @Test
    public void testStartGtxSpanWhenSpanNotNullAndHasGtx() {
        // Setup
        final String name = "name";
        final String gtx = "gtx";
        final String traceId = "4e30f7340b3fb631";
        final Span span = mock(Span.class, RETURNS_DEEP_STUBS);
        final Span newGlobalSpan = mock(Span.class);

        when(span.tags().containsKey("kermoss.gtx")).thenReturn(true);
        // Run the test
        final Span result = kermossTracerUnderTest.startGtxSpan(name, gtx, traceId, span);

        // Verify the results
        verify(newGlobalSpan, never()).tag(anyString(), anyString());
        verify(mockPublisher, never()).publishEvent(any(GtxSpanStarted.class));
        assertEquals(result, span);
    }

    @Test
    public void testStartGtxSpanWhenSpanIsNull() {
        // Setup
        final String name = "name";
        final String gtx = "gtx";
        final String traceId = "4e30f7340b3fb631";
        final Span span = null;
        final Span newGlobalSpan = mock(Span.class);
        when(mockTracer.createSpan(anyString(), any(Span.class))).thenReturn(newGlobalSpan);
        // Run the test
        final Span result = kermossTracerUnderTest.startGtxSpan(name, gtx, traceId, span);

        // Verify the results
        verify(newGlobalSpan, times(1)).tag(anyString(), anyString());
        verify(mockPublisher).publishEvent(any(GtxSpanStarted.class));
        assertEquals(result, newGlobalSpan);
    }


    @Test
    public void testStartGtxSpanWhenGlobalTransactionHasParent() {
        // Setup
        final GlobalTransaction gt = mock(GlobalTransaction.class);
        final Span span = mock(Span.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);

        doReturn(span).when(kermossTracerUnderTest).startGtxSpan(anyString(), anyString(), anyString(), any(Span.class));
        when(gt.getParent()).thenReturn("gtParent");

        // Run the test
        kermossTracerUnderTest.startGtxSpan(gt, span);

        // Verify the results
        verify(kermossTracerUnderTest).startGtxSpan(name.capture(), anyString(), anyString(), any(Span.class));
        assertEquals(name.getValue(), "In the child globalTransaction");
    }

    @Test
    public void testStartGtxSpanWhenGlobalTransactionHasNoParent() {
        // Setup
        final GlobalTransaction gt = mock(GlobalTransaction.class);
        final Span span = mock(Span.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);

        doReturn(span).when(kermossTracerUnderTest).startGtxSpan(anyString(), anyString(), anyString(), any(Span.class));
        when(gt.getParent()).thenReturn(null);

        // Run the test
        kermossTracerUnderTest.startGtxSpan(gt, span);

        // Verify the results
        verify(kermossTracerUnderTest).startGtxSpan(name.capture(), anyString(), anyString(), any(Span.class));
        assertEquals(name.getValue(),"In the parent globalTransaction");
    }

    @Test
    public void testStartNestedSpanWhenSpanNotNull() {
        // Setup
        final String name = "name";
        final String event = "event";
        final Span span = mock(Span.class);
        final Span nestedSpan = mock(Span.class);

        when(mockTracer.createSpan(anyString(), any(Span.class))).thenReturn(nestedSpan);

        // Run the test
        final Span result = kermossTracerUnderTest.startNestedSpan(name, event, span);

        // Verify the results
        verify(nestedSpan).logEvent(same(event));
        assertEquals(result, nestedSpan);

    }

    @Test
    public void testStartNestedSpanWhenSpanNull() {
        // Setup
        final String name = "name";
        final String event = "event";
        final Span span = null;
        final Span nestedSpan = mock(Span.class);

        // Run the test
        final Span result = kermossTracerUnderTest.startNestedSpan(name, event, span);

        // Verify the results
        verify(nestedSpan, never()).logEvent(same(event));
        assertEquals(result, null);
    }

    @Test
    public void testCloseSpan() {
        // Setup
        final Span span = mock(Span.class);

        // Run the test
        kermossTracerUnderTest.closeSpan(span);

        // Verify the results
        verify(mockTracer).close(same(span));
    }

    @Test
    public void testCurrentSpan() {
        // Setup
        final Span span = mock(Span.class);
        when(mockTracer.getCurrentSpan()).thenReturn(span);

        // Run the test
        final Span result = kermossTracerUnderTest.currentSpan();

        // Verify the results
        verify(mockTracer).getCurrentSpan();
        assertEquals(span, result);
    }
}
