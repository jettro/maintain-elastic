package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import nl.gridshore.dwes.index.api.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 */
@Path("/index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexResource {
    private String tempUploadStorage;
    private IndexManager indexManager;

    public IndexResource(String tempUploadStorage, IndexManager indexManager) {
        this.tempUploadStorage = tempUploadStorage;
        this.indexManager = indexManager;
    }

    @GET
    @Timed
    public List<ElasticIndex> showIndexes() {
        return indexManager.obtainIndexes();
    }

    @POST
    @Path("/{index}")
    public String changeIndex(@PathParam("index") String index, ChangeIndexRequest request) {
        request.setName(index);
        indexManager.changeIndexSettings(request);
        return "OK";
    }

    @DELETE
    @Path("/{index}")
    public String deleteIndex(@PathParam("index") String index) {
        indexManager.removeIndex(index);
        return "OK";
    }

    @POST
    @Path("/{index}/close")
    public String closeIndex(@PathParam("index") String index) {
        indexManager.closeIndex(index);
        return "OK";
    }

    @POST
    @Path("/{index}/open")
    public String openIndex(@PathParam("index") String index) {
        indexManager.openIndex(index);
        return "OK";
    }

    @POST
    @Path("/{index}/optimize")
    public String optimizeIndex(@PathParam("index") String index, @QueryParam("max") int maxSegments) {
        indexManager.optimizeIndex(new OptimizeIndexRequest(index, maxSegments));
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
        if (request.getSettings() != null) {
            String settings = readFile(request.getSettings());
            request.setSettings(settings);
            String settingsIdentifier = new ShaBasedSettingsIdentifier(settings).asString();
            request.setSettingsIdentifier(settingsIdentifier);
        }
        if (request.getMappings() != null && !request.getMappings().isEmpty()) {
            Map<String, String> newMappings = new HashMap<>();
            request.getMappings().forEach((type, filename) -> {
                newMappings.put(type, readFile(filename));
            });
            request.setMappings(newMappings);
            String mappingsIdentifier = new ShaBasedMappingsIdentifier(newMappings).asString();
            request.setMappingsIdentifier(mappingsIdentifier);
        }
        indexManager.copyIndex(request);
        return "OK";
    }

    @POST
    @Path("/{index}/createalias")
    public String createAlias(@PathParam("index") String index) {
        indexManager.createAliasFor(index);
        return "OK";
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
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(tempUploadStorage + filename))));
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
