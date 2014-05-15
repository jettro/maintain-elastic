package nl.gridshore.dwes;

import io.dropwizard.views.View;

/**
 *
 */
public class HomeView extends View {
    private final String clusterName;

    protected HomeView(String clusterName) {
        super("home.ftl");
        this.clusterName = clusterName;
    }

    public String getClusterName() {
        return clusterName;
    }
}
