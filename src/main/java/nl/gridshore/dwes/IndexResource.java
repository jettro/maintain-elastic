package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import nl.gridshore.dwes.elastic.ESClientManager;
import nl.gridshore.dwes.elastic.ElasticIndex;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 */
@Path("/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

    @POST
    @Path("/{index}")
    public String changeIndex(@PathParam("index") String index, ChangeIndexRequest request) {
        System.out.println(index);
        System.out.println(request.toString());
        // for now we only support changing number of replicas
        Map<String,Object> settings = new HashMap<>();
        settings.put("number_of_replicas",request.getNumReplicas());
        UpdateSettingsResponse updateSettingsResponse = indicesClient().prepareUpdateSettings(index)
                .setSettings(settings).execute().actionGet();
        return "OK";
    }

    private IndicesAdminClient indicesClient() {
        return clientManager.obtainClient().admin().indices();
    }

    private ClusterAdminClient clusterClient() {
        return clientManager.obtainClient().admin().cluster();
    }

}
