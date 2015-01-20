package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import nl.gridshore.dwes.elastic.ESClientManager;
import nl.gridshore.dwes.elastic.ElasticIndex;
import nl.gridshore.dwes.elastic.IndexCreator;
import nl.gridshore.dwes.elastic.ScrollAndBulkIndexContentCopier;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequestBuilder;
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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 */
@Path("/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexResource {
    private ESClientManager clientManager;
    private String tempUploadStorage;

    public IndexResource(ESClientManager esClientManager, String tempUploadStorage) {
        this.clientManager = esClientManager;
        this.tempUploadStorage = tempUploadStorage;
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
                    elasticIndex.numberOfSegments(indexStats.getPrimaries().getSegments().getCount());
                }

                indices.add(elasticIndex);
            }
        });

        return indices;
    }

    @POST
    @Path("/{index}")
    public String changeIndex(@PathParam("index") String index, ChangeIndexRequest request) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("number_of_replicas", request.getNumReplicas());
        indicesClient().prepareUpdateSettings(index).setSettings(settings).execute().actionGet();
        return "OK";
    }

    @DELETE
    @Path("/{index}")
    public String deleteIndex(@PathParam("index") String index) {
        indicesClient().prepareDelete(index).execute().actionGet();
        return "OK";
    }

    @POST
    @Path("/{index}/close")
    public String closeIndex(@PathParam("index") String index) {
        indicesClient().prepareClose(index).execute().actionGet();
        return "OK";
    }

    @POST
    @Path("/{index}/open")
    public String openIndex(@PathParam("index") String index) {
        indicesClient().prepareOpen(index).execute().actionGet();
        return "OK";
    }

    @POST
    @Path("/{index}/optimize")
    public String optimizeIndex(@PathParam("index") String index, @QueryParam("max") int maxSegments) {
        OptimizeRequestBuilder optimizeRequestBuilder = indicesClient().prepareOptimize(index);
        if (maxSegments != 0) {
            optimizeRequestBuilder.setMaxNumSegments(maxSegments);
        }
        optimizeRequestBuilder.execute().actionGet();
        return "OK";
    }

    @POST
    @Path("/settings")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFiles(@FormDataParam("file") InputStream uploadedInputStream,
                              @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String uploadedFile = fileDetail.getFileName() + randomString();
        writeToFile(uploadedInputStream, tempUploadStorage + uploadedFile);
        return uploadedFile;
    }

    @POST
    @Path("/copy")
    public String copyIndex(@Valid CopyIndexRequest request) {
        IndexCreator indexCreator = IndexCreator.build(clientManager.obtainClient(), request.getName())
                .copyFrom(request.getCopyFrom());

        if (request.getSettings() != null) {
            indexCreator.settings(readFile(request.getSettings()));
        }
        if (request.isRemoveOldAlias()) {
            indexCreator.removeOldAlias();
        }
        if (request.isRemoveOldIndices()) {
            indexCreator.removeOldIndices();
        }
        if (request.isCopyOldData()) {
            indexCreator.copyOldData(new ScrollAndBulkIndexContentCopier(clientManager.obtainClient()));
        }
        if (request.isUseIndexAsExactName()) {
            indexCreator.useIndexAsExactName();
        }
        if (request.getMappings() != null) {
            request.getMappings().keySet().stream()
                    .forEach(key -> indexCreator.addMapping(key, readFile(request.getMappings().get(key))));
        }
        indexCreator.execute();
        return "OK";
    }

    @POST
    @Path("/{index}/createalias")
    public String createAlias(@PathParam("index") String index) {
        IndexCreator.build(clientManager.obtainClient(), index)
                .replaceWithAlias()
                .copyOldData(new ScrollAndBulkIndexContentCopier(clientManager.obtainClient()))
                .execute();
        return "OK";
    }

    private IndicesAdminClient indicesClient() {
        return clientManager.obtainClient().admin().indices();
    }

    private ClusterAdminClient clusterClient() {
        return clientManager.obtainClient().admin().cluster();
    }

    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {
        try {
            OutputStream out;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String filename) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = getBufferedReader(filename)) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content.toString();
    }

    private BufferedReader getBufferedReader(String filename) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(tempUploadStorage+filename))));
    }


    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 4;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (new Random().nextFloat() * (rightLimit - leftLimit));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
