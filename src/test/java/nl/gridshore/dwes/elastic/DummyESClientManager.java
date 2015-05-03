package nl.gridshore.dwes.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * Created by jettrocoenradie on 02/05/15.
 */
public class DummyESClientManager implements ESClientManager {

    private Client client;

    public DummyESClientManager(Client client) {
        this.client = client;
    }

    @Override
    public Client obtainClient() {
        return client;
    }

    @Override
    public ClusterAdminClient obtainClusterClient() {
        return client.admin().cluster();
    }

    @Override
    public IndicesAdminClient obtainIndicesClient() {
        return client.admin().indices();
    }
}
