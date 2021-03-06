package io.kermoss.props;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class KermossPropertiesTest {

	private KermossProperties kermossProperties = new KermossProperties();

	@BeforeEach
	public void init() {
		kermossProperties.setServiceName("pizza-shop");
		Map<String, Source> destinations = new HashMap<String, Source>();
		Source destination = new Source();
		destination.setKafka("io.kermoss.test.pizza-shop");
		destination.setHttp("http://pizza-shop");
		destination.setFeign("pizza-shop");
		destinations.put("pizz-shop", destination);
		Source destination1 = new Source();
		destination1.setKafka("io.kermoss.test.market");
		destination1.setHttp("http://market");
		destinations.put("market", destination1);
		kermossProperties.setSources(destinations);
		Map<String, Sink> sinks = new HashMap<String, Sink>();
		Sink sink = new Sink();
		sink.setKafka("io.kermoss.test.pizza-shop");
		sinks.put("pizza-shop", sink);
		Sink sink1 = new Sink();
		sink1.setKafka("io.kermoss.test.market");
		sinks.put("pizza-market", sink1);
		kermossProperties.setSinks(sinks);
		
	}

	@Test
	public void testTopics() {
		List<String> topics = kermossProperties.topics();
		assertThat(topics).hasSize(2).contains("io.kermoss.test.market", "io.kermoss.test.pizza-shop");

	}

	@Test
	public void testKafkaDestination() {
		String kafkaDestination = kermossProperties.getKafkaDestination("pizz-shop");
		assertThat(kafkaDestination).isEqualTo("io.kermoss.test.pizza-shop");
	}
	
	@Test
	public void testHttpDestination() {
		String kafkaDestination = kermossProperties.getHttpDestination("pizz-shop");
		assertThat(kafkaDestination).isEqualTo("http://pizza-shop");
	}
	
	@Test
	public void testFeignDestination() {
		String kafkaDestination = kermossProperties.getFeignDestination("pizz-shop");
		assertThat(kafkaDestination).isEqualTo("pizza-shop");
	}

}