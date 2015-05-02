package nl.gridshore.dwes.elastic;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * Beware the maximum number of item to copy is what one response returns, the default is 10.
 */
public class BasicIndexContentCopier implements IndexContentCopier {

    private Client client;

    public BasicIndexContentCopier(Client client) {
        this.client = client;
    }

    @Override
    public void execute(String fromIndex, String toIndex) {
        SearchResponse searchResponse = client.prepareSearch(fromIndex).setQuery(QueryBuilders.matchAllQuery()).get();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            client.prepareIndex(toIndex, hit.getType(), hit.getId()).setSource(hit.source()).get();
        }
    }
}
