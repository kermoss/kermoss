package io.kermoss.cmd.infra.transporter.strategies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.cmd.infra.transporter.strategies.RestCommandTransporterStrategy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestCommandTransporterStrategyTest {

    @InjectMocks
    private RestCommandTransporterStrategy restCommandTransporterStrategy;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Environment environment;

    @Test
    public void transportCommandShouldSendPostRequest() {
        final TransporterCommand command = new TransporterCommand();
        final String postEndpoint = "http://kermoss";
        final ResponseEntity<TransporterCommand> response = ResponseEntity.ok(command);

        when(environment.getProperty(anyString())).thenReturn(postEndpoint);
        when(restTemplate.exchange(anyString(), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class))).thenReturn(response);

        restCommandTransporterStrategy.transportCommand(command);

        verify(restTemplate).exchange(same(postEndpoint), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class));
    }

    @Test
    public void transportCommandReturnsTrueWhenPostRequestSucceeds() {
        final TransporterCommand command = new TransporterCommand();
        final String postEndpoint = "http://kermoss";
        final ResponseEntity<TransporterCommand> response = ResponseEntity.ok(command);

        when(environment.getProperty(anyString())).thenReturn(postEndpoint);
        when(restTemplate.exchange(anyString(), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class))).thenReturn(response);

        final Boolean result = restCommandTransporterStrategy.transportCommand(command);

        assertTrue(result);
    }
}