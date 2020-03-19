package io.zeebe.cockpit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.zeebe.client.api.response.Topology;
import io.zeebe.cockpit.PartitionStats.ReplicationStatus;

public class PartitionStatus {

	private final int partitionCount;
	private final List<PartitionStats> partitionStats = new ArrayList<>();
	private final Map<ReplicationStatus, Long> partitionCountByReplicationStatus;;

	public PartitionStatus(Topology topology) {
		partitionCount = topology.getPartitionsCount();

		for (int partitionId = 1; partitionId <= partitionCount; partitionId++) {
			partitionStats.add(new PartitionStats(partitionId, topology));
		}

		partitionCountByReplicationStatus = partitionStats.stream()
				.collect(Collectors.groupingBy(PartitionStats::getReplicationStatus, Collectors.counting()));

		for (ReplicationStatus replicationStatus : ReplicationStatus.values()) {
			partitionCountByReplicationStatus.putIfAbsent(replicationStatus, Long.valueOf(0));
		}
	}

	public int getPartitionCount() {
		return partitionCount;
	}

	public List<PartitionStats> getPartitionStats() {
		return partitionStats;
	}

	public Map<ReplicationStatus, Long> getPartitionCountByReplicationStatus() {
		return partitionCountByReplicationStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(partitionCount, partitionCountByReplicationStatus, partitionStats);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartitionStatus other = (PartitionStatus) obj;
		return partitionCount == other.partitionCount
				&& Objects.equals(partitionCountByReplicationStatus, other.partitionCountByReplicationStatus)
				&& Objects.equals(partitionStats, other.partitionStats);
	}

}
