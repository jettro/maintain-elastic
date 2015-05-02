package nl.gridshore.dwes.snapshot.api;

import java.util.List;

/**
 * Manager class for interacting with the snapshot api.
 */
public interface SnapshotManager {
    List<ElasticRepository> obtainRepositories();

    ElasticSnapshotResponse findSnapshotsFor(String repository);

    void deleteRepository(String repositoryName);

    void createRepository(CreateRepositoryRequest request);

    void deleteSnapshot(DeleteSnapshotRequest request);

    void createSnapshot(CreateSnapshotRequest request);

    void restoreSnapshot(RestoreSnapshotRequest request);
}
