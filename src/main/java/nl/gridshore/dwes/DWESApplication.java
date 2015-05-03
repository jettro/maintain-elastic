package nl.gridshore.dwes;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import nl.gridshore.dwes.elastic.DefaultESClientManager;
import nl.gridshore.dwes.elastic.ESHealthCheck;
import nl.gridshore.dwes.index.DefaultIndexManager;
import nl.gridshore.dwes.snapshot.DefaultSnapshotManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DWESApplication extends Application<DWESConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(DWESApplication.class);

    public static void main(String[] args) throws Exception {
        new DWESApplication().run(args);
    }

    @Override
    public String getName() {
        return "dropwizard-elastic";
    }

    @Override
    public void initialize(Bootstrap<DWESConfiguration> dwesConfigurationBootstrap) {
        dwesConfigurationBootstrap.addBundle(new ViewBundle());
        dwesConfigurationBootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
    }

    @Override
    public void run(DWESConfiguration config, Environment environment) throws Exception {
        DefaultESClientManager esClientManager = new DefaultESClientManager(
                config.getElasticsearchHost(), config.getClusterName(), config.getUsernamePassword());
        environment.lifecycle().manage(esClientManager);

        final DefaultIndexManager indexManager = new DefaultIndexManager(esClientManager);
        environment.lifecycle().manage(indexManager);

        final DefaultSnapshotManager snapshotManager = new DefaultSnapshotManager(esClientManager);
        environment.lifecycle().manage(snapshotManager);

        logger.info("Running the application");
        final IndexResource indexResource = new IndexResource(config.getTempUploadFolder(), indexManager);
        environment.jersey().register(indexResource);

        final HomeResource homeResource = new HomeResource(config.getClusterName());
        environment.jersey().register(homeResource);

        final ClusterResource clusterResource = new ClusterResource(esClientManager);
        environment.jersey().register(clusterResource);

        final SnapshotResource snapshotResource = new SnapshotResource(snapshotManager);
        environment.jersey().register(snapshotResource);

        final ESHealthCheck esHealthCheck = new ESHealthCheck(esClientManager);
        environment.healthChecks().register("elasticsearch", esHealthCheck);

    }
}
