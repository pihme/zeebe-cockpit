package io.zeebe.cockpit;

import java.util.Objects;

import io.zeebe.client.api.response.Topology;

public class Status {

    private final boolean connected;
    private final Topology topology;
    
	public Status(boolean connected, Topology topology) {
		super();
		this.connected = connected;
		this.topology = topology;
	}

	public boolean isConnected() {
		return connected;
	}

	public Topology getTopology() {
		return topology;
	}

	@Override
	public int hashCode() {
		return Objects.hash(connected, topology);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status other = (Status) obj;
		return connected == other.connected && Objects.equals(topology, other.topology);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Status [connected=").append(connected).append(", topology=").append(topology).append("]");
		return builder.toString();
	}



}
