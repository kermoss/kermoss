package io.kermoss.cmd.domain;


public class TransporterCommand {
    private String subject;
    private String source;
    private String destination;
    private String childofGTX;
    private String FLTX;
    private String parentGTX;
    private String additionalHeaders;
    private String payload;
    private String refId;
    private String traceId;

    public TransporterCommand() {
    }

    public TransporterCommand(String subject, String source, String destination, String childofGTX, String FLTX, String parentGTX, String additionalHeaders, String payload, String refId, String traceId) {
        this.subject = subject;
        this.source = source;
        this.destination = destination;
        this.childofGTX = childofGTX;
        this.FLTX = FLTX;
        this.parentGTX = parentGTX;
        this.additionalHeaders = additionalHeaders;
        this.payload = payload;
        this.refId = refId;
        this.traceId = traceId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getChildofGTX() {
        return childofGTX;
    }

    public void setChildofGTX(String childofGTX) {
        this.childofGTX = childofGTX;
    }

    public String getFLTX() {
        return FLTX;
    }

    public void setFLTX(String FLTX) {
        this.FLTX = FLTX;
    }

    public String getParentGTX() {
        return parentGTX;
    }

    public void setParentGTX(String parentGTX) {
        this.parentGTX = parentGTX;
    }

    public String getAdditionalHeaders() {
        return additionalHeaders;
    }

    public void setAdditionalHeaders(String additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "TransporterCommand{" +
                "subject='" + subject + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", childofGTX='" + childofGTX + '\'' +
                ", FLTX='" + FLTX + '\'' +
                ", parentGTX='" + parentGTX + '\'' +
                ", additionalHeaders='" + additionalHeaders + '\'' +
                ", payload='" + payload + '\'' +
                ", refId='" + refId + '\'' +
                '}';
    }
}
