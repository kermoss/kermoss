package io.kermoss.cs.service;

import java.util.List;

import io.kermoss.cs.domain.Command;

public interface CommandService {
    List<Command> findFailedCommands();
}
