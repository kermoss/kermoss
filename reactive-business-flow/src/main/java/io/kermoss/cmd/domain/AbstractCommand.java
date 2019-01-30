package io.kermoss.cmd.domain;



import com.fasterxml.jackson.annotation.JsonIgnore;

import io.kermoss.domain.Message;

import javax.persistence.*;
import java.util.Date;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="pool")
@Table(name="KERMOSS_CMD", indexes = {
    @Index(name = "index_status", columnList = "status"),
    @Index(name = "index_s_timestamp", columnList = "startedTimestamp")
})
public abstract class AbstractCommand extends Message{
    private String subject;
    private String source;

    @JsonIgnore
    private Long startedTimestamp = new Date().getTime();
    private Long failedTimestamp;

    private String destination;
    private String GTX;
    private String LTX;
    private String FLTX;
    private String PGTX;
    private String additionalHeaders;
    private String traceId;
    @Lob
    private String payload;


    public AbstractCommand(String subject, String source, String destination, String GTX, String LTX, String FLTX, String PGTX, String additionalHeaders, String payload, String traceId) {
        this.subject = subject;
        this.source = source;
        this.destination = destination;
        this.GTX = GTX;
        this.LTX = LTX;
        this.FLTX = FLTX;
        this.PGTX = PGTX;
        this.additionalHeaders = additionalHeaders;
        this.payload = payload;
        this.traceId = traceId;
    }

    public AbstractCommand() {
    }


    public CommandMeta buildMeta() {
        return new CommandMeta(this.getId(), this.getSubject(), this.GTX, this.LTX, this.PGTX, this.FLTX, this.traceId);
    }


	public AbstractCommand(String subject, String source, String destination, String gTX, String lTX, String fLTX,
			String additionalHeaders, String payload) {
		super();
		this.subject = subject;
		this.source = source;
		this.destination = destination;
		GTX = gTX;
		LTX = lTX;
		FLTX = fLTX;
		this.additionalHeaders = additionalHeaders;
		this.payload = payload;
	}




    public String getSubject() {
        return this.subject;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }

    public String getGTX() {
        return this.GTX;
    }

    public String getLTX() {
        return this.LTX;
    }

    public String getFLTX() {
        return this.FLTX;
    }

    public String getAdditionalHeaders() {
        return this.additionalHeaders;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setGTX(String GTX) {
        this.GTX = GTX;
    }

    public void setLTX(String LTX) {
        this.LTX = LTX;
    }

    public void setFLTX(String FLTX) {
        this.FLTX = FLTX;
    }

    public void setAdditionalHeaders(String additionalHeaders) {
        this.additionalHeaders = additionalHeaders;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPGTX() {
        return PGTX;
    }

    public void setPGTX(String PGTX) {
        this.PGTX = PGTX;

    }

    public Long getStartedTimestamp() {
        return startedTimestamp;
    }

    public void setStartedTimestamp(Long startedTimestamp) {
        this.startedTimestamp = startedTimestamp;
    }

    public Long getFailedTimestamp() {
        return failedTimestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setFailedTimestamp(Long failedTimestamp) {
        this.failedTimestamp = failedTimestamp;
    }


    @Override
    public String toString() {
        return "Command{" +
                "id='" + this.getId() + '\'' +
                ", startedTimestamp=" + startedTimestamp +
                ", subject='" + subject + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", GTX='" + GTX + '\'' +
                ", LTX='" + LTX + '\'' +
                ", FLTX='" + FLTX + '\'' +
                ", PGTX='" + PGTX + '\'' +
                ", additionalHeaders='" + additionalHeaders + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
