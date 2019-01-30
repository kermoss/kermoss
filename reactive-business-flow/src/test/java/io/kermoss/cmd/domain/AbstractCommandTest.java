package io.kermoss.cmd.domain;

import org.junit.Before;
import org.junit.Test;

import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.CommandMeta;

import static org.junit.Assert.assertEquals;

public class AbstractCommandTest {

    private String subject;
    private String source;
    private String destination;
    private String gtx;
    private String ltx;
    private String fltx;
    private String pgtx;
    private String additionalHeaders;
    private String payload;
    private String traceId;

    private AbstractCommand abstractCommandUnderTest;

    @Before
    public void setUp() {
        subject = "subject";
        source = "source";
        destination = "destination";
        gtx = "GTX";
        ltx = "LTX";
        fltx = "FLTX";
        pgtx = "PGTX";
        additionalHeaders = "additionalHeaders";
        payload = "payload";
        traceId = "traceId";
        abstractCommandUnderTest = new AbstractCommand(subject, source, destination, gtx, ltx, fltx, pgtx, additionalHeaders, payload, traceId) {
        };
    }

    @Test
    public void testBuildMeta() {
        // Run the test
        final CommandMeta result = abstractCommandUnderTest.buildMeta();

        // Verify the results
        assertEquals(subject, result.getSubject());
        assertEquals(gtx, result.getGTX());
        assertEquals(ltx, result.getLTX());
        assertEquals(pgtx, result.getPGTX());
        assertEquals(fltx, result.getFLTX());
        assertEquals(traceId, result.getTraceId());
    }

}
