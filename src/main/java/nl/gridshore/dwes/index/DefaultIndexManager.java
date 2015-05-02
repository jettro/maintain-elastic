package nl.gridshore.dwes.index;

import io.dropwizard.lifecycle.Managed;
import nl.gridshore.dwes.elastic.ESClientManager;
import nl.gridshore.dwes.index.api.*;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation for the index manager
 */
public class DefaultIndexManager implements IndexManager, Managed {

    private ESClientManager esClientManager;

    public DefaultIndexManager(ESClientManager esClientManager) {
        this.esClientManager = esClientManager;
    }

    @Override
    public List<ElasticIndex> obtainIndexes() {
        ClusterStateResponse clusterState = esClientManager.obtainClusterClient().prepareState().get();
        ClusterHealthResponse clusterHealth = esClientManager.obtainClusterClient().prepareHealth().get();
        IndicesStatsResponse clusterStats = esClientManager.obtainIndicesClient().prepareStats().get();

        List<ElasticIndex> indices = new ArrayList<>();
        ImmutableOpenMap<String, IndexMetaData> stateIndices = clusterState.getState().metaData().indices();
        Map<String, ClusterIndexHealth> healthIndices = clusterHealth.getIndices();
        Map<String, IndexStats> statsIndices = clusterStats.getIndices();

        stateIndices.forEach(item -> {
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
                elasticIndex.numberOfSegments(indexStats.getPrimaries().getSegments().getCount());
            }

            indices.add(elasticIndex);
        });

        return indices;
    }

    @Override
    public void changeIndexSettings(ChangeIndexRequest request) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("number_of_replicas", request.getNumReplicas());
        esClientManager.obtainIndicesClient().prepareUpdateSettings(request.getName()).setSettings(settings).get();
    }

    @Override
    public void removeIndex(String index) {
        esClientManager.obtainIndicesClient().prepareDelete(index).get();
    }

    @Override
    public void closeIndex(String index) {
        esClientManager.obtainIndicesClient().prepareClose(index).get();
    }

    @Override
    public void openIndex(String index) {
        esClientManager.obtainIndicesClient().prepareOpen(index).get();
    }

    @Override
    public void optimizeIndex(OptimizeIndexRequest request) {
        OptimizeRequestBuilder optimizeRequestBuilder = esClientManager.obtainIndicesClient().prepareOptimize(request.getName());
        if (request.getMaxSegments() != 0) {
            optimizeRequestBuilder.setMaxNumSegments(request.getMaxSegments());
        }
        optimizeRequestBuilder.execute().actionGet();
    }

    @Override
    public void copyIndex(CopyIndexRequest request) {
        IndexCreator indexCreator = IndexCreator.build(esClientManager.obtainClient(), request.getName())
                .copyFrom(request.getCopyFrom());

        if (request.getSettings() != null) {
            indexCreator.settings(request.getSettings());
        }
        if (request.isRemoveOldAlias()) {
            indexCreator.removeOldAlias();
        }
        if (request.isRemoveOldIndices()) {
            indexCreator.removeOldIndices();
        }
        if (request.isCopyOldData()) {
            indexCreator.copyOldData(new ScrollAndBulkIndexContentCopier(esClientManager.obtainClient()));
        }
        if (request.isUseIndexAsExactName()) {
            indexCreator.useIndexAsExactName();
        }
        if (request.getMappings() != null) {
            request.getMappings().keySet().stream()
                    .forEach(key -> indexCreator.addMapping(key, request.getMappings().get(key)));
        }
        indexCreator.execute();
    }

    @Override
    public void createAliasFor(String index) {
        IndexCreator.build(esClientManager.obtainClient(), index)
                .replaceWithAlias()
                .copyOldData(new ScrollAndBulkIndexContentCopier(esClientManager.obtainClient()))
                .execute();
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}
