package io.kermoss.cmd.infra.transporter.strategies;

import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.kermoss.cmd.domain.TransporterCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.function.BiFunction;

public final class FeignCommandTransporterStrategy implements CommandTransporterStrategy {
    private final Logger log = LoggerFactory.getLogger(FeignCommandTransporterStrategy.class);
    private final Client client;
    private final BiFunction<String, Client, FeignCommandTransporterClient> clientFactory;

    public FeignCommandTransporterStrategy(final Client client, final BiFunction<String, Client, FeignCommandTransporterClient> clientFactory) {
        this.client = client;
        this.clientFactory = clientFactory;
    }

    @Override
    public Boolean transportCommand(final TransporterCommand command) {
        try {
            Response response = this.clientFactory.apply(command.getDestination(), this.client).postCommand(command);
            return response.status() == HttpStatus.ACCEPTED.value() || response.status() == HttpStatus.CONFLICT.value();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean transportPrepareCommand(final TransporterCommand command) {
        try {
            Response response = this.clientFactory.apply(command.getDestination(), this.client).prepareCommand(command);
            return response.status() == HttpStatus.ACCEPTED.value();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static FeignCommandTransporterClient defaultClientFactory(final String target, final Client client) {
        return Feign.builder()
            .client(client)
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(FeignCommandTransporterClient.class, String.format("http://%s", target));
    }

    public interface FeignCommandTransporterClient {
        @RequestLine("POST /command-executor/commands")
        @Headers("Content-Type: application/json")
        Response postCommand(final @RequestBody TransporterCommand cmd);
        @RequestLine("POST /command-executor/commands/prepare")
        @Headers("Content-Type: application/json")
        Response prepareCommand(final @RequestBody TransporterCommand cmd);
    }
}
