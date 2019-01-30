package io.kermoss.bfm.cmd;

import javax.validation.constraints.NotNull;

import io.kermoss.domain.Message;

public abstract class BaseTransactionCommand<P> extends Message {
    @NotNull
    private String subject;
    private String header;
    private P payload;
    @NotNull
    private String destination;

    @java.beans.ConstructorProperties({"subject", "header", "payload", "destination"})
    public BaseTransactionCommand(String subject, String header, P payload, String destination) {
        this.subject = subject;
        this.header = header;
        this.payload = payload;
        this.destination = destination;
    }

    public BaseTransactionCommand() {
    }

    @NotNull
    public String getSubject() {
        return this.subject;
    }

    public String getHeader() {
        return this.header;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

    @NotNull
    public String getDestination() {
        return this.destination;
    }

    public void setSubject(@NotNull String subject) {
        this.subject = subject;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setDestination(@NotNull String destination) {
        this.destination = destination;
    }
}
