package nl.gridshore.dwes.snapshot.api;

/**
 * Created by jettrocoenradie on 02/05/15.
 */
public class DeleteSnapshotRequest {
    private String repositoryName;
    private String snapshotName;

    public DeleteSnapshotRequest(String repositoryName, String snapshotName) {
        this.repositoryName = repositoryName;
        this.snapshotName = snapshotName;
    }

    public DeleteSnapshotRequest() {
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    @Override
    public String toString() {
        return "DeleteSnapshotRequest{" +
                "repositoryName='" + repositoryName + '\'' +
                ", snapshotName='" + snapshotName + '\'' +
                '}';
    }
}
