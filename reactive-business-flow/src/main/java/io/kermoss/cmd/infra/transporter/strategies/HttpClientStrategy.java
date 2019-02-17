package io.kermoss.cmd.infra.transporter.strategies;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import feign.Client;
import io.kermoss.props.KermossProperties;
import io.kermoss.props.Layer;

@Component
public class HttpClientStrategy {
	private final KermossProperties kermossProperties;
	private final RestTemplate restTemplate;
	private final Client client;

	public HttpClientStrategy(KermossProperties kermossProperties, RestTemplate restTemplate, Client client) {
		super();
		this.kermossProperties = kermossProperties;
		this.restTemplate = restTemplate;
		this.client = client;
	}

	public CommandTransporterStrategy get() {
		CommandTransporterStrategy strategy;
		Layer defaultLayer = kermossProperties.getTransport().getDefaultLayer();
		if (Layer.HTTP.equals(defaultLayer)) {
			strategy = new RestCommandTransporterStrategy(restTemplate, kermossProperties);
		} else {
			strategy = new FeignCommandTransporterStrategy(client, kermossProperties,
					FeignCommandTransporterStrategy::defaultClientFactory);
		}
		return strategy;
	};

}
