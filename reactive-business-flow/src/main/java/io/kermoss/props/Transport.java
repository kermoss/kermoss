package io.kermoss.props;

public class Transport {
	
	private Layer defaultLayer;
	

	private Kafka kafka= new Kafka();

	public Kafka getKafka() {
		return kafka;
	}

	public void setKafka(Kafka kafka) {
		this.kafka = kafka;
	}

	public Layer getDefaultLayer() {
		return defaultLayer;
	}

	public void setDefaultLayer(Layer defaultLayer) {
		this.defaultLayer = defaultLayer;
	}
	
	

}
