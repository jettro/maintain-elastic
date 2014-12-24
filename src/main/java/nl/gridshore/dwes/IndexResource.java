package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import nl.gridshore.dwes.elastic.ESClientManager;
import nl.gridshore.dwes.elastic.ElasticIndex;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
@Path("/indexes")
@Produces(MediaType.APPLICATION_JSON)
public class IndexResource {
    private ESClientManager clientManager;

    public IndexResource(ESClientManager esClientManager) {
        this.clientManager = esClientManager;
    }

    @GET
    @Timed
    public List<ElasticIndex> showIndexes() {
        ClusterStateResponse clusterState = clusterClient().prepareState().execute().actionGet();
        ClusterHealthResponse clusterHealth = clusterClient().prepareHealth().execute().actionGet();
        IndicesStatsResponse clusterStats = indicesClient().prepareStats().execute().actionGet();

        List<ElasticIndex> indices = new ArrayList<>();
        ImmutableOpenMap<String, IndexMetaData> stateIndices = clusterState.getState().metaData().indices();
        Map<String, ClusterIndexHealth> healthIndices = clusterHealth.getIndices();
        Map<String, IndexStats> statsIndices = clusterStats.getIndices();

        stateIndices.forEach(new Consumer<ObjectObjectCursor<String, IndexMetaData>>() {
            @Override
            public void accept(ObjectObjectCursor<String, IndexMetaData> item) {
                ElasticIndex elasticIndex = new ElasticIndex(item.key);
                elasticIndex.state(item.value.getState().name());
                elasticIndex.numberOfShards(item.value.numberOfShards());
                elasticIndex.numberOfReplicas(item.value.numberOfReplicas());
                ImmutableOpenMap<String, AliasMetaData> aliases = item.value.aliases();
                if (aliases.size() > 0) {
                    aliases.forEach(alias -> elasticIndex.aliases(alias.key));
                }

                ClusterIndexHealth indexHealth = healthIndices.get(item.key);
                if (indexHealth != null) {
                    elasticIndex.status(indexHealth.getStatus().name());
                }
                IndexStats indexStats = statsIndices.get(item.key);
                if (indexStats != null) {
                    elasticIndex.docCount(indexStats.getPrimaries().docs.getCount());
                    elasticIndex.size(indexStats.getPrimaries().store.size().toString());
                }

                indices.add(elasticIndex);
            }
        });

        return indices;
    }

    private IndicesAdminClient indicesClient() {
        return clientManager.obtainClient().admin().indices();
    }

    private ClusterAdminClient clusterClient() {
        return clientManager.obtainClient().admin().cluster();
    }

}
