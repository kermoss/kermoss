package io.kermoss.props;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "kermoss")
public class KermossProperties {

	private String serviceName;
	private Map<String, Destination> destinations;
	private Transport transport = new Transport();

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Map<String, Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(Map<String, Destination> destinations) {
		this.destinations = destinations;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	// TODO verify topics is empty ;
	public List<String> topics() {
		return  destinations.values().stream().map(x -> x.getKafka()).collect(Collectors.toList());
	}
	
	public String getHttpDestination(String destinationKey) {
		return destinations.get(destinationKey).getHttp();
	}
    
	public String getKafkaDestination(String destinationKey) {
		return destinations.get(destinationKey).getKafka();
	}
	public String getFeignDestination(String destinationKey) {
		return destinations.get(destinationKey).getFeign();
	}
}