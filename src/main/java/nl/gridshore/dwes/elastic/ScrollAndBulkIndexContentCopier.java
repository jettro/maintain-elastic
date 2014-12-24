package nl.gridshore.dwes.elastic;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * Created by jettrocoenradie on 24/12/14.
 * TODO fails with a closed index
 */
public class ScrollAndBulkIndexContentCopier implements IndexContentCopier {
    private static final Logger logger = LoggerFactory.getLogger(ScrollAndBulkIndexContentCopier.class);

    public static final int SCROLL_TIMEOUT_SECONDS = 60;
    public static final int SCROLL_SIZE = 100;
    public static final int BULK_ACTIONS_THRESHOLD = 100;
    public static final int BULK_CONCURRENT_REQUESTS = 1;
    public static final int BULK_FLUSH_DURATION = 30;

    private String fromIndex;
    private String toIndex;
    private Client client;

    public ScrollAndBulkIndexContentCopier(Client client) {
        this.client = client;
    }

    @Override
    public void execute(String fromIndex, String toIndex) {
        logger.info("Start copying the data from index {} to index {}", fromIndex, toIndex);
        SearchResponse searchResponse = client.prepareSearch(fromIndex)
                .setQuery(matchAllQuery())
                .setSearchType(SearchType.SCAN)
                .setScroll(createScrollTimeoutValue())
                .setSize(SCROLL_SIZE).execute().actionGet();

        BulkProcessor bulkProcessor = BulkProcessor.builder(client,
                createLoggingBulkProcessorListener()).setBulkActions(BULK_ACTIONS_THRESHOLD)
                .setConcurrentRequests(BULK_CONCURRENT_REQUESTS)
                .setFlushInterval(createFlushIntervalTime())
                .build();

        while (true) {
            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(createScrollTimeoutValue()).execute().actionGet();

            if (searchResponse.getHits().getHits().length == 0) {
                logger.info("Closing the bulk processor");
                bulkProcessor.close();
                break; //Break condition: No hits are returned
            }

            for (SearchHit hit : searchResponse.getHits()) {
                IndexRequest request = new IndexRequest(toIndex, hit.type(), hit.id());
                request.source(hit.source());
                bulkProcessor.add(request);
            }
        }
    }

    private BulkProcessor.Listener createLoggingBulkProcessorListener() {
        return new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                logger.info("Going to execute new bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                logger.info("Executed bulk composed of {} actions", request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                logger.warn("Error executing bulk", failure);
            }
        };
    }

    private TimeValue createFlushIntervalTime() {
        return new TimeValue(BULK_FLUSH_DURATION, TimeUnit.SECONDS);
    }

    private TimeValue createScrollTimeoutValue() {
        return new TimeValue(SCROLL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

}
