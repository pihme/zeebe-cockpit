package io.zeebe.cockpit;

import java.util.Objects;
import java.util.function.Predicate;

import io.zeebe.client.api.response.BrokerInfo;
import io.zeebe.client.api.response.PartitionInfo;

public class BrokerStats {

	private final int nodeId;
	private final long leaderPartitionCount;
	private final long followerPartitionCount;

	public BrokerStats(BrokerInfo brokerInfo) {
		nodeId = brokerInfo.getNodeId();

		leaderPartitionCount = brokerInfo.getPartitions().stream().filter(PartitionInfo::isLeader).count();
		followerPartitionCount = brokerInfo.getPartitions().stream().filter(Predicate.not(PartitionInfo::isLeader))
				.count();
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public long getLeaderPartitionCount() {
		return leaderPartitionCount;
	}
	
	public long getFollowerPartitionCount() {
		return followerPartitionCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(followerPartitionCount, leaderPartitionCount, nodeId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BrokerStats other = (BrokerStats) obj;
		return followerPartitionCount == other.followerPartitionCount
				&& leaderPartitionCount == other.leaderPartitionCount && nodeId == other.nodeId;
	}
	
}
