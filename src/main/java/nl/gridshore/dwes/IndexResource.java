package nl.gridshore.dwes;

import com.codahale.metrics.annotation.Timed;
import nl.gridshore.dwes.elastic.ESClientManager;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Path("/indexes")
@Produces(MediaType.APPLICATION_JSON)
public class IndexResource {
    private ESClientManager clientManager;

    public IndexResource(ESClientManager esClientManager) {
        this.clientManager = esClientManager;
    }

    @GET
    @Timed
    public List<Index> showIndexes() {
        IndicesStatusResponse indices = clientManager.obtainClient().admin().indices().prepareStatus().get();

        List<Index> result = new ArrayList<>();
        for (String key : indices.getIndices().keySet()) {
            Index index = new Index();
            index.setName(key);
            result.add(index);
        }
        return result;
    }
}
