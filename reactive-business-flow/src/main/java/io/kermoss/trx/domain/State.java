package io.kermoss.trx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;


@MappedSuperclass
public class State {
	@Id
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
	@Column(length = 36, nullable = false, updatable = false)
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
