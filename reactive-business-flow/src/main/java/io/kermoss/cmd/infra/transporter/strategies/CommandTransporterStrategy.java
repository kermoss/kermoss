package io.kermoss.cmd.infra.transporter.strategies;

import io.kermoss.cmd.domain.TransporterCommand;

public interface CommandTransporterStrategy {
    Boolean transportCommand(final TransporterCommand command);
    Boolean transportPrepareCommand(final TransporterCommand command);
}
