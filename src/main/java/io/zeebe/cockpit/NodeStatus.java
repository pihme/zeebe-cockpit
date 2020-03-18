package io.zeebe.cockpit;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.zeebe.client.api.response.BrokerInfo;
import io.zeebe.client.api.response.Topology;

public class NodeStatus {

	private final int clusterSize;
	private final int liveNodes;
	private final List<BrokerStats> brokerStats;

	public NodeStatus(Topology topology) {
		clusterSize = topology.getClusterSize();
		List<BrokerInfo> brokerInfos = topology.getBrokers();
		liveNodes = brokerInfos.size();
		brokerStats = brokerInfos.stream().map(BrokerStats::new).collect(Collectors.toList());
	}

	public int getClusterSize() {
		return clusterSize;
	}

	public int getLiveNodes() {
		return liveNodes;
	}

	public int getMissingNodes() {
		return clusterSize - liveNodes;
	}

	public List<BrokerStats> getBrokerStats() {
		return brokerStats;
	}

	@Override
	public int hashCode() {
		return Objects.hash(brokerStats, clusterSize, liveNodes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeStatus other = (NodeStatus) obj;
		return Objects.equals(brokerStats, other.brokerStats) && Objects.equals(clusterSize, other.clusterSize)
				&& Objects.equals(liveNodes, other.liveNodes);
	}

}
