package io.zeebe.cockpit;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "zeebe.cockpit")
public class Config {

	String host = "localhost";
	Integer port = 26500;

	public String getContactPoint() {
		return host + ":" + port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
