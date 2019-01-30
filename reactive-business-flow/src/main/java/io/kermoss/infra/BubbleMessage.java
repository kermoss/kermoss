package io.kermoss.infra;

import io.kermoss.domain.Message;
import io.kermoss.trx.domain.LocalTransaction;

public class BubbleMessage  {
	private String commandId;
	private final String GLTX;
	private final String LTX;
	private String FLTX;
	private String PGTX;
	private String traceId;
	private Message message;

    public BubbleMessage(LocalTransaction localTransaction) {
		this.GLTX = localTransaction.getGlobalTransaction().getId();
		this.traceId = localTransaction.getGlobalTransaction().getTraceId();
		this.LTX = localTransaction.getId();
		this.FLTX = localTransaction.getFLTX();
		this.PGTX = localTransaction.getGlobalTransaction().getParent();

	}

    @java.beans.ConstructorProperties({"GLTX", "LTX"})
    public BubbleMessage(String GLTX, String LTX) {
        this.GLTX = GLTX;
        this.LTX = LTX;
    }

    @java.beans.ConstructorProperties({"GLTX", "LTX", "FLTX"})
    public BubbleMessage(String GLTX, String LTX, String FLTX) {
        this.GLTX = GLTX;
        this.LTX = LTX;
        this.FLTX = FLTX;
    }

    public BubbleMessage(String GLTX, String LTX, String FLTX, String PGTX) {
        this.GLTX = GLTX;
        this.LTX = LTX;
        this.FLTX = FLTX;
        this.PGTX = PGTX;
    }

    public BubbleMessage(String GLTX, String LTX, String FLTX, String PGTX, Message message, String traceId) {
        this.GLTX = GLTX;
        this.LTX = LTX;
        this.FLTX = FLTX;
        this.PGTX = PGTX;
        this.message = message;
        this.traceId = traceId;
    }

    public BubbleMessage(String commandId, String GLTX, String LTX, String FLTX, String PGTX, String traceId) {
        this.commandId = commandId;
        this.GLTX = GLTX;
        this.LTX = LTX;
        this.FLTX = FLTX;
        this.PGTX = PGTX;
        this.traceId = traceId;
    }

    public Message getMessage() {
        return message;
    }

    public static BubbleMessageBuilder builder() {
        return new BubbleMessageBuilder();
    }

    public String getGLTX() {
        return this.GLTX;
    }

    public String getLTX() {
        return this.LTX;
    }

    public String getFLTX() {
        return this.FLTX;
    }

    public String getPGTX() {
        return PGTX;
    }

    public void setPGTX(String PGTX) {
        this.PGTX = PGTX;
    }

    public void setFLTX(String FLTX) {
        this.FLTX = FLTX;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class BubbleMessageBuilder<T extends Message> {
        private String commandId;
        private String GLTX;
        private String LTX;
        private String FLTX;
        private String PGTX;
        private Message message;
        private String traceId;


        BubbleMessageBuilder() {
        }

        public BubbleMessage.BubbleMessageBuilder GLTX(String GLTX) {
            this.GLTX = GLTX;
            return this;
        }

        public BubbleMessage.BubbleMessageBuilder LTX(String LTX) {
            this.LTX = LTX;
            return this;
        }

        public BubbleMessage.BubbleMessageBuilder PGTX(String PGTX) {
            this.PGTX = PGTX;
            return this;
        }

        public BubbleMessage.BubbleMessageBuilder FLTX(String FLTX) {
            this.FLTX = FLTX;
            return this;
        }

        public BubbleMessage.BubbleMessageBuilder message(Message message) {
            this.message = message;
            return this;
        }

        public BubbleMessage.BubbleMessageBuilder trace(String traceId) {
            this.traceId = traceId;
            return this;
        }
        public BubbleMessage.BubbleMessageBuilder commande(String commandId) {
            this.commandId = commandId;
            return this;
        }

        public BubbleMessage build() {
            return new BubbleMessage(commandId, GLTX, LTX, FLTX, PGTX, traceId);
        }


    }
}
