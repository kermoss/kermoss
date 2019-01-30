package io.kermoss.trx.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.Date;


@MappedSuperclass
public class State {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    protected String id;
    protected Long timestamp = new Date().getTime();
    @NotNull
    protected String name;

    @java.beans.ConstructorProperties({"id", "timestamp", "name"})
    public State(String id, Long timestamp, String name) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
    }

    public State() {
    }

    public String getId() {
        return this.id;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public String getName() {
        return this.name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }
}
