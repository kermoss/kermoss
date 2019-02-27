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
	private Map<String, Source> sources;

	private Map<String, Sink> sinks;

	private Transport transport = new Transport();

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Map<String, Source> getSources() {
		return sources;
	}

	public void setSources(Map<String, Source> sources) {
		this.sources = sources;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public Map<String, Sink> getSinks() {
		return sinks;
	}

	public void setSinks(Map<String, Sink> sinks) {
		this.sinks = sinks;
	}

	public List<String> topics() {
		return sinks.values().stream().map(x -> x.getKafka()).collect(Collectors.toList());
	}

	public String getHttpDestination(String destinationKey) {
		return sources.get(destinationKey).getHttp();
	}

	public String getKafkaDestination(String destinationKey) {
		return sources.get(destinationKey).getKafka();
	}

	public String getFeignDestination(String destinationKey) {
		return sources.get(destinationKey).getFeign();
	}

}