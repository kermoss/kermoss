package io.kermoss.cmd.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Entity
@DiscriminatorValue("OUT_CMD")
public class OutboundCommand extends AbstractCommand {
    private static final Logger log = LoggerFactory.getLogger(OutboundCommand.class);

    @Enumerated(EnumType.STRING)
	private Status status = Status.STARTED;
    private Long deliveredTimestamp;

    public OutboundCommand() {
    }

    public OutboundCommand(String subject, String source, String destination, String gTX, String lTX, String fLTX,String PGTX,
                           String additionalHeaders, Status status, String payload, String traceId) {
        super(subject, source, destination, gTX, lTX, fLTX,PGTX,additionalHeaders,payload, traceId);
        this.status=status;
    }

    public static OutboundCommandBuilder builder() {
        return new OutboundCommandBuilder();
    }

    public enum Status {
        PREPARED,
        STARTED,
        DELIVERED,
        FAILED
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeStatus(final Status status) {
        final Long timestamp = new Date().getTime();
        if(status.equals(Status.DELIVERED)) {
            this.setDeliveredTimestamp(timestamp);
        } else if(status.equals(Status.FAILED)) {
            this.setFailedTimestamp(timestamp);
        }
        this.setStatus(status);
    }

    public void changeStatusToDelivered() {
        this.changeStatus(Status.DELIVERED);
    }

    public void changeStatusToFailed() {
        this.changeStatus(Status.FAILED);
    }

    public Long getDeliveredTimestamp() {
        return deliveredTimestamp;
    }

    public void setDeliveredTimestamp(Long deliveredTimestamp) {
        this.deliveredTimestamp = deliveredTimestamp;
    }

    public static class OutboundCommandBuilder {
        private String subject;
        private String source;
        private String destination;
        private String gTX;
        private String lTX;
        private String fLTX;
        private String PGTX;
        private String additionalHeaders;
        private Status status = Status.STARTED;
        private String payload;
        private String traceId;

         OutboundCommandBuilder() {
        }

        public OutboundCommandBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public OutboundCommandBuilder source(String source) {
            this.source = source;
            return this;
        }

        public OutboundCommandBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public OutboundCommandBuilder gTX(String gTX) {
            this.gTX = gTX;
            return this;
        }
        
        
        public OutboundCommandBuilder pGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public OutboundCommandBuilder lTX(String lTX) {
            this.lTX = lTX;
            return this;
        }

        public OutboundCommandBuilder fLTX(String fLTX) {
            this.fLTX = fLTX;
            return this;
        }

        public OutboundCommandBuilder additionalHeaders(String additionalHeaders) {
            this.additionalHeaders = additionalHeaders;
            return this;
        }

        public OutboundCommandBuilder status(Status status) {
            this.status = status;
            return this;
        }

        public OutboundCommandBuilder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public OutboundCommandBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public OutboundCommand build() {
            return new OutboundCommand(subject, source, destination, gTX, lTX, fLTX,PGTX, additionalHeaders, status, payload, traceId);
        }

        public String toString() {
            return "OutboundCommand.OutboundCommandBuilder(subject=" + this.subject + ", source=" + this.source + ", destination=" + this.destination + ", gTX=" + this.gTX + ", lTX=" + this.lTX + ", fLTX=" + this.fLTX + ", additionalHeaders=" + this.additionalHeaders + ", status=" + this.status + ", payload=" + this.payload + ")";
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
