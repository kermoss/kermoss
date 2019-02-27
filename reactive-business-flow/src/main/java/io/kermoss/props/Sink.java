package io.kermoss.props;

public class Sink {
	
	private String kafka;

	

	public String getKafka() {
		return kafka;
	}

	public void setKafka(String kafka) {
		this.kafka = kafka;
	}

	@Override
	public String toString() {
		return "Sink [kafka=" + kafka + "]";
	}

	
     
	
}
