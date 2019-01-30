package io.kermoss.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
public abstract class Message {

    @Id
    @JsonProperty("commandId")
    @NotNull
    private String id = UUID.randomUUID().toString();
    @JsonProperty("startedTimestamp")
    private final Long timestamp = new Date().getTime();


    public Message() {
    }

    @NotNull
    public String getId() {
        return id;
    }
}
