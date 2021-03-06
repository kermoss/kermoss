package io.kermoss.cmd.infra.transporter.strategies;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import feign.Client;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import io.kermoss.cmd.domain.TransporterCommand;

@ExtendWith(MockitoExtension.class)
public class FeignCommandTransporterStrategyTest {

    @Mock
    private BiFunction<String, Client, FeignCommandTransporterStrategy.FeignCommandTransporterClient> clientFactory;
    @Mock
    private FeignCommandTransporterStrategy.FeignCommandTransporterClient client;

    @InjectMocks
    private FeignCommandTransporterStrategy feignCommandTransporterStrategy;

    @Test
    public void transportCommandShouldSendPostRequest() {
        final TransporterCommand command = new TransporterCommand();
        final Response response = Response.builder().request(Request.create(HttpMethod.POST,"/", new HashMap<>(), "".getBytes(),Charset.defaultCharset())).status(202).headers(new HashMap<>()).build();

        when(clientFactory.apply(any(), any())).thenReturn(client);
        when(client.postCommand(any())).thenReturn(response);

        feignCommandTransporterStrategy.transportCommand(command);

        verify(client).postCommand(command);
    }

    @Test
    public void transportCommandReturnsTrueWhenPostRequestSucceeds() {
        final TransporterCommand command = new TransporterCommand();
        final Response response = Response.builder().request(Request.create(HttpMethod.POST,"/", new HashMap<>(), "".getBytes(),Charset.defaultCharset())).status(202).headers(new HashMap<>()).build();

        when(clientFactory.apply(any(), any())).thenReturn(client);
        when(client.postCommand(any())).thenReturn(response);

        final Boolean result = feignCommandTransporterStrategy.transportCommand(command);

        assertTrue(result);
    }

}