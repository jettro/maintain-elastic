package nl.gridshore.dwes.snapshot;

import io.dropwizard.lifecycle.Managed;
import nl.gridshore.dwes.elastic.ESClientManager;
import nl.gridshore.dwes.snapshot.api.*;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotStatus;
import org.elasticsearch.action.admin.cluster.snapshots.status.SnapshotsStatusResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.snapshots.SnapshotInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Default implementation for the SnapshotManager
 */
public class DefaultSnapshotManager implements SnapshotManager, Managed {

    private ESClientManager esClientManager;

    public DefaultSnapshotManager(ESClientManager esClientManager) {
        this.esClientManager = esClientManager;
    }

    @Override
    public List<ElasticRepository> obtainRepositories() {
        GetRepositoriesResponse repositoriesResponse = esClientManager.obtainClusterClient()
                .prepareGetRepositories().execute().actionGet();

        return repositoriesResponse.repositories().asList().stream()
                .map(this::mapFrom)
                .collect(Collectors.toList());

    }

    @Override
    public ElasticSnapshotResponse findSnapshotsFor(String repository) {
        SnapshotsStatusResponse snapshotsStatusResponse = esClientManager.obtainClusterClient().prepareSnapshotStatus().get();
        ImmutableList<SnapshotStatus> snapshotStatuses = snapshotsStatusResponse.getSnapshots().asList();
        if (snapshotStatuses.size() > 0) {
            List<ElasticSnapshotStatus> snapshots = snapshotStatuses.stream().map(this::mapFrom).collect(Collectors.toList());
            return ElasticSnapshotResponse.runningSnapshots(snapshots);
        } else {
            GetSnapshotsResponse snapshotsResponse = esClientManager.obtainClusterClient().prepareGetSnapshots(repository).get();

            List<ElasticSnapshot> snapshots = snapshotsResponse.getSnapshots().asList().stream()
                    .map((info) -> mapFrom(repository, info))
                    .collect(Collectors.toList());
            return ElasticSnapshotResponse.availableSnapshots(snapshots);
        }
    }

    @Override
    public void deleteRepository(String repositoryName) {
        esClientManager.obtainClusterClient().prepareDeleteRepository(repositoryName).get();
    }

    @Override
    public void createRepository(CreateRepositoryRequest request) {
        String type = "fs";
        Map<String, Object> settings = new HashMap<>();
        if ("readonly".equals(request.getType())) {
            type = "url";
            settings.put("url", request.getLocation());
        } else {
            settings.put("location", request.getLocation());
        }

        esClientManager.obtainClusterClient().preparePutRepository(request.getName())
                .setType(type)
                .setSettings(settings)
                .get();

    }

    @Override
    public void deleteSnapshot(DeleteSnapshotRequest request) {
        esClientManager.obtainClusterClient()
                .prepareDeleteSnapshot(request.getRepositoryName(), request.getSnapshotName()).get();
    }

    @Override
    public void createSnapshot(CreateSnapshotRequest request) {
        String name = request.getName();
        if (StringUtils.isEmpty(name)) {
            name = request.getPrefix() + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
        CreateSnapshotRequestBuilder snapshotRequestBuilder = esClientManager.obtainClusterClient()
                .prepareCreateSnapshot(request.getRepository(), name)
                .setIncludeGlobalState(request.getIncludeGlobalState())
                .setIndicesOptions(IndicesOptions.fromOptions(request.getIgnoreUnavailable(), false, true, false));
        if (StringUtils.isEmpty(request.getIndexes())) {
            snapshotRequestBuilder.setIndices("_all");
        } else {
            snapshotRequestBuilder.setIndices(request.getIndexes().split(","));
        }
        snapshotRequestBuilder.get();
    }

    @Override
    public void restoreSnapshot(RestoreSnapshotRequest request) {
        RestoreSnapshotRequestBuilder builder = esClientManager.obtainClusterClient()
                .prepareRestoreSnapshot(request.getRepository(), request.getSnapshot())
                .setIncludeAliases(request.isIncludeAliases())
                .setRestoreGlobalState(request.isIncludeGlobalState());
        if (isNotEmpty(request.getIndexes())) {
            builder.setIndices(request.getIndexes().split(","));
        }
        if (isNotEmpty(request.getRenamePattern())) {
            builder.setRenamePattern(request.getRenamePattern());
        }
        if (isNotEmpty(request.getRenameReplacement())) {
            if (isEmpty(request.getRenamePattern()) && isNotEmpty(request.getIndexes())) {
                builder.setRenamePattern(request.getIndexes());
            }
            builder.setRenameReplacement(request.getRenameReplacement());
        }
        builder.get();
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

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
                repository.setLocation(meta.settings().get("url"));
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

    private ElasticSnapshot mapFrom(String repositoryName, SnapshotInfo info) {
        ElasticSnapshot elasticSnapshot = new ElasticSnapshot(info.name());
        elasticSnapshot.setRepository(repositoryName);
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
