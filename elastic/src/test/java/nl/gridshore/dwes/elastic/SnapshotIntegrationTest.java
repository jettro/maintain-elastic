package nl.gridshore.dwes.elastic;

import nl.gridshore.dwes.snapshot.DefaultSnapshotManager;
import nl.gridshore.dwes.snapshot.api.*;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertHitCount;

/**
 * Created by jettrocoenradie on 02/05/15.
 */
public class SnapshotIntegrationTest extends ElasticsearchIntegrationTest {

    public static final String TEMP_INDEX = "temp-index";
    public static final String TEMP_INDEX_TWO = "temp-index-two";
    public static final String TEMP_REPO = "temprepo";
    public static final String FIRST_SNAPSHOT = "firstsnapshot";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private DefaultSnapshotManager snapshotManager;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.snapshotManager = new DefaultSnapshotManager(new DummyESClientManager(client()));
    }

    @Test
    public void checkBasicRestore() throws IOException, InterruptedException {
        createIndex(TEMP_INDEX);
        addDocumentsAndFlush(TEMP_INDEX);

        preparerepository();

        // Create a snapshot
        CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest();
        snapshotRequest.setRepository(TEMP_REPO);
        snapshotRequest.setName(FIRST_SNAPSHOT);
        snapshotRequest.setIndexes(TEMP_INDEX);
        this.snapshotManager.createSnapshot(snapshotRequest);

        waitForSnapshotToFinish(TEMP_REPO);

        client().admin().indices().prepareDelete(TEMP_INDEX).get();

        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest();
        restoreSnapshotRequest.setRepository(TEMP_REPO);
        restoreSnapshotRequest.setIndexes(TEMP_INDEX);
        restoreSnapshotRequest.setSnapshot(FIRST_SNAPSHOT);
        this.snapshotManager.restoreSnapshot(restoreSnapshotRequest);

        checkRecoveredSnapshot(TEMP_REPO, TEMP_INDEX);

    }

    @Test
    public void checkBasicRestore_rename() throws IOException, InterruptedException {
        createIndex(TEMP_INDEX);
        addDocumentsAndFlush(TEMP_INDEX);

        preparerepository();

        // Create a snapshot
        CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest();
        snapshotRequest.setRepository(TEMP_REPO);
        snapshotRequest.setName(FIRST_SNAPSHOT);
        snapshotRequest.setIndexes(TEMP_INDEX);
        this.snapshotManager.createSnapshot(snapshotRequest);

        waitForSnapshotToFinish(TEMP_REPO);

        client().admin().indices().prepareDelete(TEMP_INDEX).get();

        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest();
        restoreSnapshotRequest.setRepository(TEMP_REPO);
        restoreSnapshotRequest.setIndexes(TEMP_INDEX);
        restoreSnapshotRequest.setSnapshot(FIRST_SNAPSHOT);
        restoreSnapshotRequest.setRenameReplacement(TEMP_INDEX_TWO);
        this.snapshotManager.restoreSnapshot(restoreSnapshotRequest);

        checkRecoveredSnapshot(TEMP_REPO, TEMP_INDEX_TWO);
    }

    @Test
    public void checkRestoreOneOutOfTwo() throws IOException, InterruptedException {
        createIndex(TEMP_INDEX);
        addDocumentsAndFlush(TEMP_INDEX);
        createIndex(TEMP_INDEX_TWO);
        addDocumentsAndFlush(TEMP_INDEX_TWO);

        preparerepository();

        // Create a snapshot
        CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest();
        snapshotRequest.setRepository(TEMP_REPO);
        snapshotRequest.setName(FIRST_SNAPSHOT);
        this.snapshotManager.createSnapshot(snapshotRequest);

        waitForSnapshotToFinish(TEMP_REPO);

        client().admin().indices().prepareDelete(TEMP_INDEX).get();
        client().admin().indices().prepareDelete(TEMP_INDEX_TWO).get();


        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest();
        restoreSnapshotRequest.setRepository(TEMP_REPO);
        restoreSnapshotRequest.setIndexes(TEMP_INDEX);
        restoreSnapshotRequest.setSnapshot(FIRST_SNAPSHOT);
        this.snapshotManager.restoreSnapshot(restoreSnapshotRequest);

        checkRecoveredSnapshot(TEMP_REPO, TEMP_INDEX);

        assertFalse(client().admin().indices().prepareExists(TEMP_INDEX_TWO).get().isExists());
    }

    @Test
    public void checkRestoreTwoOutOfTwo() throws IOException, InterruptedException {
        createIndex(TEMP_INDEX);
        addDocumentsAndFlush(TEMP_INDEX);
        createIndex(TEMP_INDEX_TWO);
        addDocumentsAndFlush(TEMP_INDEX_TWO);

        preparerepository();

        // Create a snapshot
        CreateSnapshotRequest snapshotRequest = new CreateSnapshotRequest();
        snapshotRequest.setRepository(TEMP_REPO);
        snapshotRequest.setName(FIRST_SNAPSHOT);
        this.snapshotManager.createSnapshot(snapshotRequest);

        waitForSnapshotToFinish(TEMP_REPO);

        client().admin().indices().prepareDelete(TEMP_INDEX).get();
        client().admin().indices().prepareDelete(TEMP_INDEX_TWO).get();


        RestoreSnapshotRequest restoreSnapshotRequest = new RestoreSnapshotRequest();
        restoreSnapshotRequest.setRepository(TEMP_REPO);
        restoreSnapshotRequest.setIndexes(TEMP_INDEX + "," + TEMP_INDEX_TWO);
        restoreSnapshotRequest.setSnapshot(FIRST_SNAPSHOT);
        this.snapshotManager.restoreSnapshot(restoreSnapshotRequest);

        checkRecoveredSnapshot(TEMP_REPO, TEMP_INDEX);
        checkRecoveredSnapshot(TEMP_REPO, TEMP_INDEX_TWO);

    }

    private void checkRecoveredSnapshot(String repo, String index) throws InterruptedException {
        ElasticSnapshotResponse temprepo = this.snapshotManager.findSnapshotsFor(repo);
        while (temprepo.getRunningSnapshot()) {
            Thread.sleep(100);
            temprepo = this.snapshotManager.findSnapshotsFor(repo);
        }

        CountResponse countResponse = client().prepareCount(index).get();
        int tries = 0;
        while (countResponse.getTotalShards() != countResponse.getSuccessfulShards() || tries == 5) {
            Thread.sleep(100);
            countResponse = client().prepareCount(index).get();
            tries++;
        }

        assertHitCount(countResponse, 5);
    }

    private void waitForSnapshotToFinish(String repo) throws InterruptedException {
        // do something to wait for snapshot to finish
        ElasticSnapshotResponse temprepo = this.snapshotManager.findSnapshotsFor(repo);
        while (temprepo.getRunningSnapshot()) {
            Thread.sleep(100);
            temprepo = this.snapshotManager.findSnapshotsFor(repo);
        }
        assertEquals(1, temprepo.getSnapshots().size());
    }

    private void preparerepository() throws IOException {
        List<ElasticRepository> elasticRepositories = this.snapshotManager.obtainRepositories();
        assertEquals(0, elasticRepositories.size());

        CreateRepositoryRequest request = new CreateRepositoryRequest();
        request.setName(TEMP_REPO);
        request.setLocation(getTempFolder());
        request.setType("fs");
        snapshotManager.createRepository(request);

        elasticRepositories = this.snapshotManager.obtainRepositories();
        assertEquals(1, elasticRepositories.size());
    }

    private String getTempFolder() throws IOException {
        return testFolder.getRoot().getCanonicalPath();
    }

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
