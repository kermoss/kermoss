package io.kermoss.cmd.domain;

import org.junit.Before;
import org.junit.Test;

import io.kermoss.cmd.domain.AbstractCommand;
import io.kermoss.cmd.domain.CommandMeta;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertEquals(subject, abstractCommandUnderTest.getSubject());
        assertEquals(gtx, abstractCommandUnderTest.getGTX());
        assertEquals(ltx, abstractCommandUnderTest.getLTX());
        assertEquals(pgtx, abstractCommandUnderTest.getPGTX());
        assertEquals(fltx, abstractCommandUnderTest.getFLTX());
        assertEquals(traceId, abstractCommandUnderTest.getTraceId());
        assertEquals(destination, abstractCommandUnderTest.getDestination());
    }
    
    @Test
    public void testSubject() {
    	abstractCommandUnderTest.setSubject("subject");
    	assertThat(abstractCommandUnderTest.getSubject()).isEqualTo("subject");
    }
    
    @Test
    public void testAdditionalHeaders() {
    	abstractCommandUnderTest.setAdditionalHeaders("aaaa");
    	assertThat(abstractCommandUnderTest.getAdditionalHeaders()).isEqualTo("aaaa");
    	
    }
    @Test
    public void testDestination() {
    	abstractCommandUnderTest.setDestination("aaaa");
    	assertThat(abstractCommandUnderTest.getDestination()).isEqualTo("aaaa");
    	
    }
    @Test
    public void testGtx() {
    	abstractCommandUnderTest.setGTX("aaaa");
    	assertThat(abstractCommandUnderTest.getGTX()).isEqualTo("aaaa");
    }
    
    @Test
    public void testLtx() {
    	abstractCommandUnderTest.setLTX("aaaa");
    	assertThat(abstractCommandUnderTest.getLTX()).isEqualTo("aaaa");
    }
    
    @Test
    public void testFltx() {
    	abstractCommandUnderTest.setFLTX("aaaa");
    	assertThat(abstractCommandUnderTest.getFLTX()).isEqualTo("aaaa");
    }
    
    @Test
    public void testPayLoad() {
    	abstractCommandUnderTest.setPayload("aAaaa");
    	assertThat(abstractCommandUnderTest.getPayload()).isEqualTo("aAaaa");
    }
    
    @Test
    public void testPgtx() {
    	abstractCommandUnderTest.setPGTX("aAaaa");
    	assertThat(abstractCommandUnderTest.getPGTX()).isEqualTo("aAaaa");
    }
    
    @Test
    public void testStartedTimestamp() {
    	Long a = System.currentTimeMillis();
    	abstractCommandUnderTest.setStartedTimestamp(a);
    	assertThat(abstractCommandUnderTest.getStartedTimestamp()).isEqualTo(a);
    	
    }
    @Test
    public void testFailedimestamp() {
    	Long a = System.currentTimeMillis();
    	abstractCommandUnderTest.setFailedTimestamp(a);
    	assertThat(abstractCommandUnderTest.getFailedTimestamp()).isEqualTo(a);
    	
    }
    
    @Test
    public void testTraceId() {
    	String a = "WWWWWWWWW";
    	abstractCommandUnderTest.setTraceId(a);
    	assertThat(abstractCommandUnderTest.getTraceId()).isEqualTo(a);
    	
    }
    

}
