package io.kermoss.cmd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TransporterCommandTest {
    
    @Test
	public void testTransportCommand() {
	   TransporterCommand transporterCommand = new TransporterCommand();
	   transporterCommand.setSubject("subject");
	   assertThat(transporterCommand.getSubject()).isEqualTo("subject");
       transporterCommand.setChildofGTX("parentGtx");
       assertThat(transporterCommand.getChildofGTX()).isEqualTo("parentGtx");
       transporterCommand.setDestination("destination");
       assertThat(transporterCommand.getDestination()).isEqualTo("destination");
       transporterCommand.setFLTX("FLTX");
       assertThat(transporterCommand.getFLTX()).isEqualTo("FLTX");
       transporterCommand.setRefId("refId");
       assertThat(transporterCommand.getRefId()).isEqualTo("refId");
       transporterCommand.setSource("source");
       assertThat(transporterCommand.getSource()).isEqualTo("source");
       transporterCommand.setTraceId("traceId");
       assertThat(transporterCommand.getTraceId()).isEqualTo("traceId");
       transporterCommand.setPayload("payload");
       assertThat(transporterCommand.getPayload()).isEqualTo("payload");
       transporterCommand.setAdditionalHeaders("addH");
       assertThat(transporterCommand.getAdditionalHeaders()).isEqualTo("addH");
       transporterCommand.setParentGTX("parentGTX");
       assertThat(transporterCommand.getParentGTX()).isEqualTo("parentGTX");
       transporterCommand.toString();
    }
	
	
}
