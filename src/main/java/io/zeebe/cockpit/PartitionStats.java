package io.zeebe.cockpit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.zeebe.client.api.response.BrokerInfo;
import io.zeebe.client.api.response.Topology;

public class PartitionStats {

	public static enum ReplicationStatus {
		OVERREPLICATED, IDEAL, UNDERREPLICATED, MISSING
	}

	private final int partitionId;
	private final int replicationFactor;
	private final int replicaCount;
	private final ReplicationStatus replicationStatus;
	private final List<Integer> leaders = new ArrayList<>();
	private final List<Integer> followers = new ArrayList<>();

	public PartitionStats(int id, Topology topology) {
		partitionId = id;
		replicationFactor = topology.getReplicationFactor();

		for (BrokerInfo brokerInfo : topology.getBrokers()) {
			brokerInfo.getPartitions().forEach(p -> {
				if (p.getPartitionId() == partitionId) {
					if (p.isLeader()) {
						leaders.add(brokerInfo.getNodeId());
					} else {
						followers.add(brokerInfo.getNodeId());
					}
				}
			});
		}

		replicaCount = leaders.size() + followers.size();

		if (replicaCount > replicationFactor) {
			replicationStatus = ReplicationStatus.OVERREPLICATED;
		} else if (replicaCount == replicationFactor) {
			replicationStatus = ReplicationStatus.IDEAL;
		} else if (replicaCount > 0) {
			replicationStatus = ReplicationStatus.UNDERREPLICATED;
		} else {
			replicationStatus = ReplicationStatus.MISSING;
		}
	}

	public int getPartitionId() {
		return partitionId;
	}

	public int getReplicationFactor() {
		return replicationFactor;
	}

	public int getReplicaCount() {
		return replicaCount;
	}

	public ReplicationStatus getReplicationStatus() {
		return replicationStatus;
	}

	public List<Integer> getLeaders() {
		return leaders;
	}

	public List<Integer> getFollowers() {
		return followers;
	}

	@Override
	public int hashCode() {
		return Objects.hash(followers, leaders, partitionId, replicaCount, replicationFactor, replicationStatus);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartitionStats other = (PartitionStats) obj;
		return Objects.equals(followers, other.followers) && Objects.equals(leaders, other.leaders)
				&& partitionId == other.partitionId && replicaCount == other.replicaCount
				&& replicationFactor == other.replicationFactor && replicationStatus == other.replicationStatus;
	}

}
