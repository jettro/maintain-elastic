package nl.gridshore.dwes;

import nl.gridshore.dwes.elastic.*;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotStatus;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.snapshots.SnapshotInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all requests related to snapshots.
 */
@Path("/repository")
@Produces(MediaType.APPLICATION_JSON)
public class SnapshotResource {
    private ESClientManager clientManager;

    public SnapshotResource(ESClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @GET
    public List<ElasticRepository> showRepositories() {
        GetRepositoriesResponse repositoriesResponse = clusterClient()
                .prepareGetRepositories().execute().actionGet();

        return repositoriesResponse.repositories().asList().stream()
                .map(this::mapFrom)
                .collect(Collectors.toList());
    }

    /**
     * We first check the status of the current running snapshots. For performance reasons we do not obtain all
     * snapshots and their properties while a snapshot is being created.
     *
     * @param repositoryName String containing the name of the repository to check the snapshots for.
     * @return
     */
    @GET
    @Path("/{repositoryName}/snapshots")
    public ElasticSnapshotResponse showSnapshots(@PathParam("repositoryName") String repositoryName) {
        SnapshotsStatusResponse snapshotsStatusResponse = clusterClient().prepareSnapshotStatus().execute().actionGet();
        ImmutableList<SnapshotStatus> snapshotStatuses = snapshotsStatusResponse.getSnapshots().asList();
        if (snapshotStatuses.size() > 0) {
            List<ElasticSnapshotStatus> snapshots = snapshotStatuses.stream().map(this::mapFrom).collect(Collectors.toList());
            return ElasticSnapshotResponse.runningSnapshots(snapshots);
        } else {
            GetSnapshotsResponse snapshotsResponse = clusterClient()
                    .prepareGetSnapshots(repositoryName).execute().actionGet();

            List<ElasticSnapshot> snapshots = snapshotsResponse.getSnapshots().asList().stream()
                    .map(this::mapFrom)
                    .collect(Collectors.toList());
            return ElasticSnapshotResponse.availableSnapshots(snapshots);
        }
    }

    @DELETE
    @Path("/{repositoryName}")
    public void deleteRepository(@PathParam("repositoryName") String repositoryName) {
        clusterClient().prepareDeleteRepository(repositoryName).execute().actionGet();
    }

    private ClusterAdminClient clusterClient() {
        return clientManager.obtainClient().admin().cluster();
    }

    private ElasticRepository mapFrom(RepositoryMetaData meta) {
        ElasticRepository repository = new ElasticRepository();
        repository.setName(meta.name());
        repository.setType(meta.type());
        switch (meta.type()) {
            case "fs":
                repository.setLocation(meta.settings().get("location"));
                break;
            case "url":
                repository.setLocation(meta.settings().get("location"));
                break;
            case "s3":
                repository.setLocation(meta.settings().get("bucket"));
                break;
            case "hdfs":
                repository.setLocation(meta.settings().get("path"));
                break;
            case "":
                repository.setLocation(meta.settings().get("base_path"));
                break;
        }
        return repository;
    }

    private ElasticSnapshot mapFrom(SnapshotInfo info) {
        ElasticSnapshot elasticSnapshot = new ElasticSnapshot(info.name());
        elasticSnapshot.startTime(info.startTime());
        elasticSnapshot.endTime(info.endTime());
        elasticSnapshot.setState(info.state().name());
        elasticSnapshot.setStatus(info.status().name());
        info.indices().asList().stream().forEach(elasticSnapshot::addIndex);
        if (info.failedShards() > 0) {
            info.shardFailures().asList().asList().forEach(snapshotShardFailure ->
                    elasticSnapshot.addError(snapshotShardFailure.index(), snapshotShardFailure.reason()));
        }
        return elasticSnapshot;
    }

    private ElasticSnapshotStatus mapFrom(SnapshotStatus status) {
        ElasticSnapshotStatus elasticSnapshotStatus = new ElasticSnapshotStatus();
        elasticSnapshotStatus.setRepository(status.getSnapshotId().getRepository());
        elasticSnapshotStatus.setSnapshot(status.getSnapshotId().getSnapshot());
        elasticSnapshotStatus.setNumberOfFiles(status.getStats().getNumberOfFiles());
        elasticSnapshotStatus.setNumberOfProcessedFiles(status.getStats().getProcessedFiles());
        elasticSnapshotStatus.setTotalSize(status.getStats().getTotalSize());
        elasticSnapshotStatus.setProcessedSize(status.getStats().getProcessedSize());
        elasticSnapshotStatus.startTime(status.getStats().getStartTime());
        elasticSnapshotStatus.setState(status.getState().name());
        return elasticSnapshotStatus;
    }
}
