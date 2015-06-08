package nl.gridshore.dwes;

import nl.gridshore.dwes.snapshot.api.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Handles all requests related to snapshots.
 */
@Path("/repository")
@Produces(MediaType.APPLICATION_JSON)
public class SnapshotResource {
    private SnapshotManager snapshotManager;

    public SnapshotResource(SnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
    }

    @GET
    public List<ElasticRepository> showRepositories() {
        return snapshotManager.obtainRepositories();
    }

    /**
     * We first check the status of the current running snapshots. For performance reasons we do not obtain all
     * snapshots and their properties while a snapshot is being created.
     *
     * @param repositoryName String containing the name of the repository to check the snapshots for.
     * @return The snapshots for the provided repository or the running snapshot
     */
    @GET
    @Path("/{repositoryName}/snapshots")
    public ElasticSnapshotResponse showSnapshots(@PathParam("repositoryName") String repositoryName) {
        return snapshotManager.findSnapshotsFor(repositoryName);
    }

    @DELETE
    @Path("/{repositoryName}")
    public void deleteRepository(@PathParam("repositoryName") String repositoryName) {
        snapshotManager.deleteRepository(repositoryName);
    }

    @POST
    public void createRepository(CreateRepositoryRequest request) {
        snapshotManager.createRepository(request);
    }

    @DELETE
    @Path("/{repositoryName}/snapshot/{snapshotName}")
    public void deleteSnapshot(@PathParam("repositoryName") String repositoryName,
                               @PathParam("snapshotName") String snapshotName) {
        snapshotManager.deleteSnapshot(new DeleteSnapshotRequest(repositoryName, snapshotName));
    }

    @POST
    @Path("/{repositoryName}/snapshot")
    public void createSnapshot(@PathParam("repositoryName") String repositoryName, CreateSnapshotRequest request) {
        request.setRepository(repositoryName);
        snapshotManager.createSnapshot(request);
    }

    @POST
    @Path("/{repositoryName}/snapshot/{snapshotName}")
    public void restoreSnapshot(@PathParam("repositoryName") String repositoryName,
                                @PathParam("snapshotName") String snapshotName,
                                RestoreSnapshotRequest request) {
        request.setRepository(repositoryName);
        request.setSnapshot(snapshotName);
        snapshotManager.restoreSnapshot(request);
    }
}
