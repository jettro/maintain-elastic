package nl.gridshore.dwes;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.gridshore.dwes.elastic.ESClientFactorybean;
import nl.gridshore.dwes.elastic.ESHealthCheck;
import org.elasticsearch.client.Client;
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
    }

    @Override
    public void run(DWESConfiguration config, Environment environment) throws Exception {
        Client client = ESClientFactorybean.obtainClient(config.getElasticsearchHost(), config.getClusterName());

        logger.info("Running the application");
        final IndexResource indexResource = new IndexResource(client);
        environment.jersey().register(indexResource);

        final ESHealthCheck esHealthCheck = new ESHealthCheck(client);
        environment.healthChecks().register("elasticsearch", esHealthCheck);
    }
}
