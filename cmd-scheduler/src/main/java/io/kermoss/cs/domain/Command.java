package io.kermoss.cs.domain;

public class Command {
    private String id;
    private String status;

    public Command() {
    }

    public Command(final String id, final String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
