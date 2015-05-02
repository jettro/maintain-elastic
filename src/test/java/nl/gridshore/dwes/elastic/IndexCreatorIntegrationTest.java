package nl.gridshore.dwes.elastic;

import nl.gridshore.dwes.index.IndexCreator;
import nl.gridshore.dwes.index.IndexCreatorConfigException;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertHitCount;

/**
 * Test class to go over all scenario's supported by the IndexCreator class.
 */
public class IndexCreatorIntegrationTest extends ElasticsearchIntegrationTest {
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");

    @Test
    public void checkBasicCreate() {
        String index1 = "basic-create-1";
        IndexCreator.build(client(), index1).execute();
        assertTrue(checkAliasExists(index1));
        String index = checkIndexForAlias(index1);
        assertTrue(indexExists(index));
    }

    @Test
    public void checkBasicCreateNoAlias() {
        String index = "basic-create-2";
        IndexCreator.build(client(), index).useIndexAsExactName().execute();
        assertFalse(checkAliasExists(index));
        assertTrue(indexExists(index));
    }

    @Test
    public void checkBasicCopy() throws IOException {
        String originalIndex = "basic-create-original-3";
        String originalIndexName = createIndexWithAlias(originalIndex);

        addDocumentsAndFlush(originalIndex);
        CountResponse countResponse = client().prepareCount(originalIndex).get();
        assertHitCount(countResponse, 5);

        String newIndex = "basic-create-3";
        IndexCreator.build(client(), newIndex)
                .copyFrom(originalIndexName)
                .copyOldData(new BasicIndexContentCopier(client()))
                .execute();

        flushAndRefresh(newIndex);

        assertTrue(indexExists(newIndex));
        assertTrue(indexExists(originalIndex));

        countResponse = client().prepareCount(newIndex).get();
        assertHitCount(countResponse, 5);
    }

    @Test(expected = IndexCreatorConfigException.class)
    public void checkBasicCopy_nonexistingindex() throws IOException {
        String originalIndex = "basic-create-original-4";
        createIndexWithAlias(originalIndex);

        addDocumentsAndFlush(originalIndex);

        String newIndex = "basic-create-4";

        IndexCreator.build(client(), newIndex)
                .copyFrom("nonexisting")
                .copyOldData(new BasicIndexContentCopier(client()))
                .execute();
    }

    @Test(expected = IndexCreatorConfigException.class)
    public void checkBasicCopy_usealiastocopyfrom() throws IOException {
        String originalIndex = "basic-create-original-5";
        createIndexWithAlias(originalIndex);

        addDocumentsAndFlush(originalIndex);

        String newIndex = "basic-create-5";

        IndexCreator.build(client(), newIndex)
                .copyFrom(originalIndex)
                .copyOldData(new BasicIndexContentCopier(client()))
                .execute();
    }

    @Test
    public void checkBasicCopyAndRemove() throws IOException {
        String originalIndex = "basic-create-original-6";
        String originalIndexName = createIndexWithAlias(originalIndex);

        addDocumentsAndFlush(originalIndex);

        String newIndex = "basic-create-6";
        IndexCreator.build(client(), newIndex)
                .copyFrom(originalIndexName)
                .copyOldData(new BasicIndexContentCopier(client()))
                .removeOldIndices()
                .execute();

        flushAndRefresh(newIndex);

        assertTrue(indexExists(newIndex));
        assertFalse(indexExists(originalIndex));

        SearchResponse searchResponse = client().prepareSearch(newIndex).get();
        assertHitCount(searchResponse, 5);
        assertTrue(checkAliasExists(newIndex));
        assertFalse(checkAliasExists(originalIndex));

        checkIndexForAlias(newIndex);
    }

    @Test
    public void checkBasicCopyAndRemove_nonexistingoriginalindex() throws IOException {
        String newIndex = "basic-create-7";
        IndexCreator.build(client(), newIndex)
                .copyOldData(new BasicIndexContentCopier(client()))
                .removeOldIndices()
                .execute();

        flushAndRefresh(newIndex);

        assertTrue(indexExists(newIndex));

        SearchResponse searchResponse = client().prepareSearch(newIndex).get();
        assertHitCount(searchResponse, 0);
        assertTrue(checkAliasExists(newIndex));

        checkIndexForAlias(newIndex);
    }

    @Test
    public void checkBasicCopyReIndex() throws IOException {
        String alias = "basic-create-8";
        String originalIndexName = createIndexWithAlias(alias);

        addDocumentsAndFlush(alias);

        IndexCreator.build(client(), alias)
                .copyOldData(new BasicIndexContentCopier(client()))
                .removeOldIndices()
                .execute();

        flushAndRefresh(alias);

        assertFalse(indexExists(originalIndexName));
        assertTrue(checkAliasExists(alias));

        SearchResponse searchResponse = client().prepareSearch(alias).get();
        assertHitCount(searchResponse, 5);
        assertTrue(checkAliasExists(alias));

        String newIndex = checkIndexForAlias(alias);
        assertNotEquals(newIndex, originalIndexName);
    }


    // Utility methods
    private boolean checkAliasExists(String alias) {
        AliasesExistResponse aliasesExistResponse = client().admin().indices().prepareAliasesExist(alias).get();
        return aliasesExistResponse.exists();
    }

    private String checkIndexForAlias(String alias) {
        GetAliasesResponse getAliasesResponse = client().admin().indices().prepareGetAliases(alias).get();
        assertEquals(1, getAliasesResponse.getAliases().keys().size());
        return getAliasesResponse.getAliases().keys().iterator().next().value;
    }

    private String createIndexWithAlias(String alias) {
        String indexName = alias + "-" + LocalDateTime.now().format(dateTimeFormatter);
        createIndex(indexName);
        IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client().admin().indices().prepareAliases();
        indicesAliasesRequestBuilder.addAlias(indexName, alias).get();
        return indexName;
    }

    // Preparing the data
    private void addDocumentsAndFlush(String originalIndex) throws IOException {

        doIndexAndFlush(originalIndex, "Jettro Coenradie", "1");
        doIndexAndFlush(originalIndex, "Marijn Coenradie", "2");
        doIndexAndFlush(originalIndex, "Roberto van der Linden", "3");
        doIndexAndFlush(originalIndex, "Ralph Broers", "4");
        doIndexAndFlush(originalIndex, "Sander Pagie", "5");
    }

    private void doIndexAndFlush(String originalIndex, String name, String identifier) throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("name", name)
                .endObject();

        client().prepareIndex(originalIndex, "base", identifier).setSource(builder).get();
        flushAndRefresh(originalIndex);
    }


}
