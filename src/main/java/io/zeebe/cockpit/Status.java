package io.zeebe.cockpit;

import java.util.Objects;

import io.zeebe.client.api.response.Topology;

public class Status {

	private final boolean connected;
	private final NodeStatus nodeStatus;
	private final PartitionStatus partitionStatus;
	private final Topology topology;

	public Status(boolean connected, Topology topology) {
		super();
		this.connected = connected;
		this.topology = topology;
		if (topology != null) {
			nodeStatus = new NodeStatus(topology);
			partitionStatus = new PartitionStatus(topology);
		} else {
			nodeStatus = null;
			partitionStatus = null;
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

	public PartitionStatus getPartitionStatus() {
		return partitionStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(connected, nodeStatus, partitionStatus);
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
		return connected == other.connected && Objects.equals(nodeStatus, other.nodeStatus)
				&& Objects.equals(partitionStatus, other.partitionStatus);
	}

}
