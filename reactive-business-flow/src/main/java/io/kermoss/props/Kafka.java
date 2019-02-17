package io.kermoss.props;

public class Kafka {
	
	private String bootstrapAddress;
    private Consumer consumer = new Consumer();
	
	
	
	public String getBootstrapAddress() {
		return bootstrapAddress;
	}

	public void setBootstrapAddress(String bootstrapAddress) {
		this.bootstrapAddress = bootstrapAddress;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}
}