package io.kermoss.props;

public class Transport {
	private BrokerMode brokerMode;
	private Layer defaultLayer;
	private Kafka kafka= new Kafka();

	public BrokerMode getBrokerMode() {
		return brokerMode;
	}

	public void setBrokerMode(BrokerMode brokerMode) {
		this.brokerMode = brokerMode;
	}

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
