package nl.gridshore.dwes.elastic;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This object is meant to be used once to create a new index based on an existing index or on data to be imported.
 * Creating an index consists of a few steps:
 * <ul>
 * <li>Create the index with a timestamp, add settings ans mappings,</li>
 * <li>If wanted import data from other index,</li>
 * <li>If wanted import data by other means,</li>
 * <li>Check existence of an index and move it or create it to new index.</li>
 * </ul>
 */
public class IndexCreator {
    private static final Logger logger = LoggerFactory.getLogger(IndexCreator.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private String index;
    private Client client;
    private String indexName;
    private CreateIndexRequestBuilder indexBuilder;

    private boolean removeOldIndices = false;
    private boolean removeOldAlias = false;

    private IndexCreator(Client client, String index) {
        this.client = client;
        this.index = index;
        this.indexName = this.index + "-" + LocalDateTime.now().format(dateTimeFormatter);
        this.indexBuilder = client.admin().indices().prepareCreate(this.indexName);
    }

    /* API Methods */
    public static IndexCreator start(Client client, String index) {
        logger.debug("IndexCreator for index {} is started", index);
        return new IndexCreator(client, index);
    }

    public void create() {
        if (removeOldIndices) {
            createAndRemove();
        }
    }

    /* Fluent interface setter methods */
    public IndexCreator settings(String settings) {
        this.indexBuilder.setSettings(settings);
        return this;
    }

    public IndexCreator addMapping(String type, String mapping) {
        this.indexBuilder.addMapping(type,mapping);
        return this;
    }

    public IndexCreator removeOldIndices() {
        this.removeOldIndices = true;
        this.removeOldAlias = true;
        return this;
    }

    public IndexCreator removeOldAlias() {
        this.removeOldAlias = true;
        return this;
    }

    /* private worker methods */
    private void createAndRemove() {
        List<String> indexesToBeRemoved = obtainIndicesForAlias();

        createIndex();

        indexesToBeRemoved.stream().forEach(this::removeIndex);
    }

    private void createIndex() {
        indexBuilder.execute().actionGet();
        moveAlias();
    }

    private void moveAlias() {
        IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
        if (removeOldAlias) {
            indicesAliasesRequestBuilder.removeAlias(index + "-*", index);
        }
        indicesAliasesRequestBuilder.addAlias(indexName, index)
                .execute().actionGet();
    }

    private List<String> obtainIndicesForAlias() {
        List<String> indicesToBeRemoved = new ArrayList<>();

        ImmutableOpenMap<String, List<AliasMetaData>> aliases =
                client.admin().indices().getAliases(new GetAliasesRequest(index)).actionGet().getAliases();
        aliases.keysIt().forEachRemaining(indicesToBeRemoved::add);
        return indicesToBeRemoved;
    }

    private void removeIndex(String indexName) {
        client.admin().indices().prepareDelete(indexName).execute().actionGet();
    }
}
