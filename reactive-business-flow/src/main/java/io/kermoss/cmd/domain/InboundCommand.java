package io.kermoss.cmd.domain;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity
@DiscriminatorValue("IN_CMD")
public class InboundCommand extends AbstractCommand {

    @Enumerated(EnumType.STRING)
    private Status status = Status.STARTED;
    private Long completedTimestamp;
    private String refId;

    public InboundCommand() {
    }

    public InboundCommand(String subject, String source, String destination, String GTX, String LTX, String FLTX, String PGTX, String additionalHeaders, String payload, Status status, String refId, String traceId) {
        super(subject, source, destination, GTX, LTX, FLTX, PGTX, additionalHeaders, payload, traceId);
        this.status = status;
        this.refId = refId;
    }
    
    public InboundCommand( String subject, String source, String destination, String GTX, String LTX, String FLTX, String PGTX, String additionalHeaders, String payload, Status status, String traceId) {
        super(subject, source, destination, GTX, LTX, FLTX, PGTX, additionalHeaders, payload, traceId);
        this.status = status;
    }

    public static InboundCommandBuilder builder() {
        return new InboundCommandBuilder();
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus(final Status status) {
        final Long timestamp = new Date().getTime();
        if(status.equals(Status.COMPLETED)) {
            this.setCompletedTimestamp(timestamp);
        } else if(status.equals(Status.FAILED)) {
            this.setFailedTimestamp(timestamp);
        }
        this.setStatus(status);
    }

    public void changeStatusToCompleted() {
        this.changeStatus(Status.COMPLETED);
    }

    public void changeStatusToFailed() {
        this.changeStatus(Status.FAILED);
    }

    public Long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(Long completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public enum Status {
        PREPARED,
        STARTED,
        COMPLETED,
        FAILED
    }

    


    public static class InboundCommandBuilder {
        private String subject;
        private String source;
        private String destination;
        private String gTX;
        private String lTX;
        private String fLTX;
        private String PGTX;
        private String additionalHeaders;
        private Status status = Status.STARTED;
        private String traceId;

        private String payload;
        private String refId;

        InboundCommandBuilder() {
        }

        public InboundCommandBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public InboundCommandBuilder source(String source) {
            this.source = source;
            return this;
        }

        public InboundCommandBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public InboundCommandBuilder gTX(String gTX) {
            this.gTX = gTX;
            return this;
        }

        public InboundCommandBuilder lTX(String lTX) {
            this.lTX = lTX;
            return this;
        }

        public InboundCommandBuilder fLTX(String fLTX) {
            this.fLTX = fLTX;
            return this;
        }

        public InboundCommandBuilder PGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public InboundCommandBuilder additionalHeaders(String additionalHeaders) {
            this.additionalHeaders = additionalHeaders;
            return this;
        }

        public InboundCommandBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public InboundCommandBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public InboundCommandBuilder refId(String refId) {
            this.refId= refId;
            return this;
        }

        public InboundCommandBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public InboundCommand build() {
            return new InboundCommand(subject, source, destination, gTX, lTX, fLTX, PGTX, additionalHeaders, payload, status, refId, traceId);
        }

        @Override
        public String toString() {
            return "InboundCommandBuilder{" +
                    "subject='" + subject + '\'' +
                    ", source='" + source + '\'' +
                    ", destination='" + destination + '\'' +
                    ", gTX='" + gTX + '\'' +
                    ", lTX='" + lTX + '\'' +
                    ", fLTX='" + fLTX + '\'' +
                    ", PGTX='" + PGTX + '\'' +
                    ", additionalHeaders='" + additionalHeaders + '\'' +
                    ", status=" + status +
                    ", payload='" + payload + '\'' +
                    ", refId='" + refId + '\'' +
                    '}';
        }
    }
}
