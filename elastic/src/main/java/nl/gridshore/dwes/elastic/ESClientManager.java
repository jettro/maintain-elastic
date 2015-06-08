package nl.gridshore.dwes.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * Created by jettrocoenradie on 02/05/15.
 */
public interface ESClientManager {
    Client obtainClient();

    ClusterAdminClient obtainClusterClient();

    IndicesAdminClient obtainIndicesClient();
}
