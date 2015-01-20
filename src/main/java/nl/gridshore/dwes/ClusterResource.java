package nl.gridshore.dwes;

import nl.gridshore.dwes.ClusterStatus;
import nl.gridshore.dwes.elastic.ESClientManager;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterAdminClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/cluster")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterResource {

    private ESClientManager clientManager;

    public ClusterResource(ESClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @GET
    @Path("/status")
    public ClusterStatus clusterStatus() {
        ClusterHealthResponse clusterIndexHealths = obtainClusterClient().prepareHealth().execute().actionGet();
        String clusterName = clusterIndexHealths.getClusterName();
        switch (clusterIndexHealths.getStatus()) {
            case GREEN:
                return new ClusterStatus(clusterName, "success");
            case YELLOW:
                return new ClusterStatus(clusterName, "warn");
            case RED:
            default:
                return new ClusterStatus(clusterName, "danger");
        }
    }

    private ClusterAdminClient obtainClusterClient() {
        return clientManager.obtainClient().admin().cluster();
    }
}
