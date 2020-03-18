package io.zeebe.cockpit;

import io.zeebe.client.api.response.Topology;

public class Status {

	private final boolean connected;
	private final NodeStatus nodeStatus;
	private final Topology topology;

	public Status(boolean connected, Topology topology) {
		super();
		this.connected = connected;
		this.topology = topology;
		if (topology != null) {
			nodeStatus = new NodeStatus(topology);
		} else {
			nodeStatus = null;
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public Topology getTopology() {
		return topology;
	}

	public NodeStatus getNodeStatus() {
		return nodeStatus;
	}
	
	

}
