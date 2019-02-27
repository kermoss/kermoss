package io.kermoss.cmd.infra.transporter.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.kermoss.cmd.domain.TransporterCommand;
import io.kermoss.props.KermossProperties;

public final class RestCommandTransporterStrategy implements CommandTransporterStrategy {
    private final Logger log = LoggerFactory.getLogger(RestCommandTransporterStrategy.class);
    private final RestTemplate restTemplate;
    private final KermossProperties kermossProperties;

    public RestCommandTransporterStrategy(final RestTemplate restTemplate, 
    		KermossProperties kermossProperties) {
        this.restTemplate = restTemplate;
        this.kermossProperties = kermossProperties;
    }

    @Override
    public Boolean transportCommand(final TransporterCommand command) {
        try {
            final ResponseEntity<TransporterCommand> response = this.postCommand(command);
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        }catch (HttpClientErrorException hce) {
            if (hce.getStatusCode().equals(HttpStatus.CONFLICT)) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean transportPrepareCommand(final TransporterCommand command) {
        try {
            final ResponseEntity<TransporterCommand> response = this.prepareCommand(command);
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }
        }catch (HttpClientErrorException hce) {
            if (hce.getStatusCode().equals(HttpStatus.CONFLICT)) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private ResponseEntity<TransporterCommand> postCommand(final TransporterCommand command) {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<TransporterCommand> entity = new HttpEntity<>(command, httpHeaders);

        return restTemplate.exchange(
                this.calculateDestination(command.getDestination()),
                HttpMethod.POST,
                entity,
                TransporterCommand.class
        );
    }

    private ResponseEntity<TransporterCommand> prepareCommand(final TransporterCommand command) {
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<TransporterCommand> entity = new HttpEntity<>(command, httpHeaders);

        return restTemplate.exchange(
            this.calculateDestination(command.getDestination()).concat("/prepare"),
            HttpMethod.POST,
            entity,
            TransporterCommand.class
        );
    }

    private String calculateDestination(final String destinationKey) {
		String topic = kermossProperties.getHttpDestination(destinationKey); 
		return topic;
	}
}
