package nl.gridshore.dwes.elastic;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This object is meant to be used once to create a new index based on an existing index or on data to be imported.
 * There are a number of things to configure before you create the index and start copying data. Below the options are
 * explained.</p>
 * <p>We prefer to create an index with a timestamp, create an alias without the timestamp and use the alias in your
 * program. The defaults for this class follow that scenario. In this case you provide the name of the alias in the
 * constructor. There is an alternative scenario, you can also create an index without a timestamp, than you provided
 * the actual name of the index and not the alias when creating this class. Configure this using
 * {@link #useIndexAsExactName()}.</p>
 * <p>You can manually configure the index to copy settings {@link #copyFrom(String)}, mappings and data from. The
 * default is however to take the index the alias points to. If the alias points to multiple indices we take the last
 * one.</p>
 * <p>Settings and Mappings can be provided as a string, the default is to copy the settings and mappings from the
 * index taken from copyFrom or using the alias.</p>
 * <p>You can use {@link #removeOldIndices()} to remove the copyFrom index or the indices the alias points to after the
 * creation and copy part have been done. You can also only remove the alias from the old index using
 * {@link #removeOldAlias()}</p>
 * </p>
 * <p>The final bit to configure is to copy the data from the old data into the new index. This is a little bit more
 * effort. You have to provide an implementation of {@link nl.gridshore.dwes.elastic.IndexContentCopier} to do the
 * actual copying. An example implementation is the {@link nl.gridshore.dwes.elastic.ScrollAndBulkIndexContentCopier}</p>
 */
public class IndexCreator {
    private static final Logger logger = LoggerFactory.getLogger(IndexCreator.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private String index;
    private Client client;
    private CreateIndexRequestBuilder indexBuilder;
    private IndexContentCopier indexContentCopier;

    private String indexName;
    private String settings;
    private Map<String, String> mappings;
    private boolean removeOldIndices = false;
    private boolean removeOldAlias = false;
    private boolean indexIsExactName = false;
    private String copyFrom;
    private List<String> indicesForAlias;

    private IndexCreator(Client client, String index) {
        this.client = client;
        this.index = index;
    }

    /* API Methods */

    /**
     * Initialize the index builder using the elasticsearch client and the name of the index or alias.
     *
     * @param client Client with connection to elasticsearch cluster
     * @param index  String containing the name for the index or the alias
     * @return the created IndexCreator
     */
    public static IndexCreator build(Client client, String index) {
        logger.debug("IndexCreator for index {} is started", index);
        return new IndexCreator(client, index);
    }

    public void execute() {
        initializeIndexToCreate();

        findIndexToCopyFromAndIntializeFromAlias();

        initializeSettings();

        initializeMappings();

        executeIndexCreation();

        copyDataIfRequired();

        moveAliasIfRequired();

        removeOldIndicesIfRequired();
    }
    
    /* Fluent interface setter methods */

    /**
     * By providing the settings you indicate you do not want the settings from an original index. If you are creating
     * a new index, this is mandatory.
     *
     * @param settings String containing the source for the elasticsearch index settings
     * @return part of fluent interface
     */
    public IndexCreator settings(String settings) {
        this.settings = settings;
        return this;
    }

    /**
     * Add a mapping for specified type. By specifying a mapping you indicate you do not want to copy mappings from
     * an existing index. If you are creating a new index, this is mandatory.
     *
     * @param type    String containing the type to add a mapping for
     * @param mapping String containing the actual mapping for the provided type
     * @return part of fluent interface
     */
    public IndexCreator addMapping(String type, String mapping) {
        if (this.mappings == null) {
            this.mappings = new HashMap<>();
        }
        this.mappings.put(type, mapping);
        return this;
    }

    /**
     * Calling this function indicates you want to remove the old indices. This can be the provided copyFrom index or
     * based on the index found by the fact you are using a prefix.
     *
     * @return part of fluent interface
     */
    public IndexCreator removeOldIndices() {
        this.removeOldIndices = true;
        this.removeOldAlias = true;
        return this;
    }

    /**
     * Use this to configure that the alias is to be removed from the old index.
     *
     * @return part of fluent interface
     */
    public IndexCreator removeOldAlias() {
        this.removeOldAlias = true;
        return this;
    }

    /**
     * Use this to configure that all the data from the copyFrom index needs to be copied to the new index.
     *
     * @param copier Instance of IndexContentCopier that is called to do the copying of data
     * @return part of fluent interface
     */
    public IndexCreator copyOldData(IndexContentCopier copier) {
        this.indexContentCopier = copier;
        return this;
    }

    /**
     * Manually set the index to copy from, only to be used when not using the aliases.
     *
     * @param indexToCopyFrom String containing the name of the index to copy data and settings from
     * @return part of fluent interface
     */
    public IndexCreator copyFrom(String indexToCopyFrom) {
        this.copyFrom = indexToCopyFrom;
        return this;
    }

    /**
     * Means that you do not want the provided index to be the prefix of the actual index and the name of the alias.
     *
     * @return part of fluent interface
     */
    public IndexCreator useIndexAsExactName() {
        this.indexIsExactName = true;
        return this;
    }

    /* private worker methods */
    private void moveAlias() {
        IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
        if (removeOldAlias) {
            indicesAliasesRequestBuilder.removeAlias(index + "-*", index);
        }
        indicesAliasesRequestBuilder.addAlias(indexName, index)
                .execute().actionGet();
    }

    private List<String> obtainIndicesForAlias() {
        List<String> foundIndices = new ArrayList<>();

        ImmutableOpenMap<String, List<AliasMetaData>> aliases =
                client.admin().indices().getAliases(new GetAliasesRequest(index)).actionGet().getAliases();
        aliases.keysIt().forEachRemaining(foundIndices::add);
        return foundIndices;
    }

    private void removeIndex(String indexName) {
        client.admin().indices().prepareDelete(indexName).execute().actionGet();
    }

    private void removeOldIndicesIfRequired() {
        if (removeOldIndices) {
            if (this.indicesForAlias.size() > 0) {
                this.indicesForAlias.stream().forEach(this::removeIndex);
            } else {
                removeIndex(this.copyFrom);
            }
        }
    }

    private void moveAliasIfRequired() {
        if (!indexIsExactName) {
            moveAlias();
        }
    }

    private void copyDataIfRequired() {
        if (this.indexContentCopier != null) {
            indexContentCopier.execute(copyFrom, indexName);
        }
    }

    private void executeIndexCreation() {
        indexBuilder.execute().actionGet();
    }

    private void initializeMappings() {
        if (mappings == null) {
            if (copyFrom != null) {
                GetMappingsResponse getMappingsResponse =
                        client.admin().indices().prepareGetMappings(this.copyFrom).execute().actionGet();
                ImmutableOpenMap<String, MappingMetaData> mappingsForIndex =
                        getMappingsResponse.getMappings().get(this.copyFrom);
                mappingsForIndex.forEach(item -> {
                    try {
                        this.indexBuilder.addMapping(item.key, item.value.sourceAsMap());
                    } catch (IOException e) {
                        logger.warn("Could not add the mapping for {} from the index {}", item.key, this.copyFrom, e);
                    }
                });
            }
        } else {
            mappings.forEach(this.indexBuilder::addMapping);
        }
    }

    private void initializeSettings() {
        if (settings == null) {
            if (copyFrom != null) {
                GetSettingsResponse getSettingsResponse =
                        client.admin().indices().prepareGetSettings(this.copyFrom).execute().actionGet();
                ImmutableOpenMap<String, Settings> indexToSettings = getSettingsResponse.getIndexToSettings();
                this.indexBuilder.setSettings(indexToSettings.get(this.copyFrom));
            }
        } else {
            this.indexBuilder.setSettings(this.settings);
        }
    }

    private void findIndexToCopyFromAndIntializeFromAlias() {
        if (!this.indexIsExactName) {
            this.indicesForAlias = obtainIndicesForAlias();
            if (this.indicesForAlias.size() > 0) {
                this.copyFrom = this.indicesForAlias.get(this.indicesForAlias.size() - 1);
            }
        }
    }

    private void initializeIndexToCreate() {
        if (indexIsExactName) {
            this.indexName = this.index;
        } else {
            this.indexName = this.index + "-" + LocalDateTime.now().format(dateTimeFormatter);
        }
        this.indexBuilder = client.admin().indices().prepareCreate(this.indexName);
    }

}
