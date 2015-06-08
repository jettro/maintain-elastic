package nl.gridshore.dwes.snapshot.api;

import java.util.List;

/**
 * Combined value object for a snapshot status or a list of snapshots
 */
public class ElasticSnapshotResponse {
    private List<ElasticSnapshotStatus> snapshotStatussus;
    private List<ElasticSnapshot> snapshots;
    private Boolean runningSnapshot;

    private ElasticSnapshotResponse(List<ElasticSnapshot> snapshots,
                                   List<ElasticSnapshotStatus> snapshotStatussus,
                                   Boolean runningSnapshot) {
        this.snapshots = snapshots;
        this.snapshotStatussus = snapshotStatussus;
        this.runningSnapshot = runningSnapshot;
    }

    public static ElasticSnapshotResponse availableSnapshots(List<ElasticSnapshot> snapshots) {
        return new ElasticSnapshotResponse(snapshots,null,false);
    }

    public static ElasticSnapshotResponse runningSnapshots(List<ElasticSnapshotStatus> snapshotStatussus) {
        return new ElasticSnapshotResponse(null,snapshotStatussus,true);
    }

    public List<ElasticSnapshot> getSnapshots() {
        return snapshots;
    }

    public List<ElasticSnapshotStatus> getSnapshotStatussus() {
        return snapshotStatussus;
    }

    public Boolean getRunningSnapshot() {
        return runningSnapshot;
    }
}
