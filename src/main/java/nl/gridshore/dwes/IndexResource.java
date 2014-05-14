package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.client.Client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Path("/index")
@Produces(MediaType.APPLICATION_JSON)
public class IndexResource {
    private Client client;

    public IndexResource(Client client) {
        this.client = client;
    }

    @GET
    @Timed
    public List<Index> showIndexes() {
        IndicesStatusResponse indices = client.admin().indices().prepareStatus().get();

        List<Index> result = new ArrayList<>();
        for (String key : indices.getIndices().keySet()) {
            Index index = new Index();
            index.setName(key);
            result.add(index);
        }
        return result;
    }
}
