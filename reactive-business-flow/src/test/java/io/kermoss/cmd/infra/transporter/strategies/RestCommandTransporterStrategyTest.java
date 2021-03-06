package io.kermoss.cmd.infra.transporter.strategies;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;
import static org.mockito.ArgumentMatchers.nullable;
@ExtendWith(MockitoExtension.class)
public class RestCommandTransporterStrategyTest {

    @InjectMocks
    private RestCommandTransporterStrategy restCommandTransporterStrategy;

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private KermossProperties kermossProperties;
     
    
    @Test
    public void transportCommandShouldSendPostRequest() {
        final TransporterCommand command = new TransporterCommand();
        final String postEndpoint = "http://kermoss";
        final ResponseEntity<TransporterCommand> response = ResponseEntity.ok(command);
       
        when(kermossProperties.getHttpDestination(nullable(String.class))).thenReturn(postEndpoint);
        
        when(restTemplate.exchange(anyString(), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class))).thenReturn(response);

        restCommandTransporterStrategy.transportCommand(command);

        verify(restTemplate).exchange(same(postEndpoint), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class));
    }

    @Test
    public void transportCommandReturnsTrueWhenPostRequestSucceeds() {
        final TransporterCommand command = new TransporterCommand();
        final String postEndpoint = "http://kermoss";
        final ResponseEntity<TransporterCommand> response = ResponseEntity.ok(command);
        when(kermossProperties.getHttpDestination(nullable(String.class))).thenReturn(postEndpoint);
        when(restTemplate.exchange(anyString(), same(HttpMethod.POST), any(HttpEntity.class), same(TransporterCommand.class))).thenReturn(response);

        final Boolean result = restCommandTransporterStrategy.transportCommand(command);

        assertTrue(result);
    }
}