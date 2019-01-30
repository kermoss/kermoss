package io.kermoss.cs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import io.kermoss.cs.domain.Command;

import java.util.Date;
import java.util.List;

@Service
public final class CommandServiceImpl implements CommandService {
    private final JdbcTemplate jdbcTemplate;
    private final Long maxCommandExecutionTime;

    @Autowired
    public CommandServiceImpl(final JdbcTemplate jdbcTemplate, @Value("${kermoss.cs.config.max-cmd-exec-time}") final Long maxCommandExecutionTime) {
        this.jdbcTemplate = jdbcTemplate;
        this.maxCommandExecutionTime = maxCommandExecutionTime;
    }

    public List<Command> findFailedCommands() {
        final Long currentTimestamp = new Date().getTime();
        return jdbcTemplate.query(
            "SELECT id, status FROM KERMOSS_CMD cmd WHERE cmd.status = 'FAILED' " +
                String.format("OR (cmd.status = 'STARTED' AND (cmd.started_timestamp + %d) < %d)", maxCommandExecutionTime, currentTimestamp),
            new BeanPropertyRowMapper(Command.class)
        );
    }
}
