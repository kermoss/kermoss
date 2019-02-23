package io.kermoss.props;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KermossPropertiesTest {

	private KermossProperties kermossProperties = new KermossProperties();

	@Before
	public void init() {
		kermossProperties.setServiceName("pizza-shop");
		Map<String, Destination> destinations = new HashMap<String, Destination>();
		Destination destination = new Destination();
		destination.setKafka("io.kermoss.test.pizza-shop");
		destination.setHttp("http://pizza-shop");
		destination.setFeign("pizza-shop");
		destinations.put("pizz-shop", destination);
		Destination destination1 = new Destination();
		destination1.setKafka("io.kermoss.test.market");
		destination1.setHttp("http://market");
		destinations.put("market", destination1);
		kermossProperties.setDestinations(destinations);
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