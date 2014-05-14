package nl.gridshore.dwes.elastic;

import com.codahale.metrics.health.HealthCheck;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;

/**
 * This is a healthcheck implementation for elasticsearch. It makes use of the status returned by the cluster.
 */
public class ESHealthCheck extends HealthCheck {

    private Client client;

    public ESHealthCheck(Client client) {
        this.client = client;
    }

    @Override
    protected Result check() throws Exception {
        ClusterHealthResponse clusterIndexHealths = client.admin().cluster().health(new ClusterHealthRequest())
                .actionGet();
        switch (clusterIndexHealths.getStatus()) {
            case GREEN:
                return HealthCheck.Result.healthy();
            case YELLOW:
                return HealthCheck.Result.unhealthy("Cluster state is yellow, maybe replication not done? New Nodes?");
            case RED:
            default:
                return HealthCheck.Result.unhealthy("Something is very wrong with the cluster", clusterIndexHealths);

        }
    }
}
