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
		if(Layer.KAFKA.equals(defaultLayer)) {
			if(kermossProperties.getTransport().getBrokerMode()==null || kermossProperties.getTransport().getBrokerMode().getPreparedRequestUse()==null ) {
				throw new IllegalArgumentException("configure PreparedRequestUse property in path transport.broker-mode");
			}
			
			if(kermossProperties.getTransport().getBrokerMode().getPreparedRequestUse().equals(Layer.KAFKA) ) {
				throw new IllegalArgumentException("PreparedRequestUse doesnot yet support kafka layer please think to use feign or http");
			}
			
			defaultLayer=kermossProperties.getTransport().getBrokerMode().getPreparedRequestUse();
		}
		
		if (Layer.HTTP.equals(defaultLayer)) {
			strategy = new RestCommandTransporterStrategy(restTemplate, kermossProperties);
		} else {
			strategy = new FeignCommandTransporterStrategy(client, kermossProperties,
					FeignCommandTransporterStrategy::defaultClientFactory);
		}
		return strategy;
	};

}
