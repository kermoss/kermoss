package io.kermoss.props;

public class Destination {
	
	private String http;
	private String feign;
	private String kafka;

	public String getHttp() {
		return http;
	}

	public void setHttp(String http) {
		this.http = http;
	}

	public String getFeign() {
		return feign;
	}

	public void setFeign(String feign) {
		this.feign = feign;
	}

	public String getKafka() {
		return kafka;
	}

	public void setKafka(String kafka) {
		this.kafka = kafka;
	}

	@Override
	public String toString() {
		return "Destination [http=" + http + ", feign=" + feign + ", kafka=" + kafka + "]";
	}
     
	
}
