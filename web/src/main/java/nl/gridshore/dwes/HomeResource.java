package nl.gridshore.dwes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class HomeResource {
    private String clusterName;

    public HomeResource(String clusterName) {
        this.clusterName = clusterName;
    }


    @GET
    public HomeView goHome() {
        return new HomeView(clusterName);
    }
}
