package nl.gridshore.dwes.elastic;

import io.dropwizard.lifecycle.Managed;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * Elasticsearch client factory bean that returns a client instance. You are responsible for closing the client when
 * you are done with it. Client objects are expensive to use and should be reused within your application.
 * <p/>
 * The host string most be of format "host1:port1,host2:port2"
 * The cluster name must be the name of the cluster than runs on the provided host(s)
 */
public class ESClientManager implements Managed {
    private static final Logger logger = LoggerFactory.getLogger(ESClientManager.class);

    private final List<String> hosts;
    private final String clusterName;

    private Client client;

    public ESClientManager(String host, String clusterName) {
        this.hosts = Arrays.asList(host.split(","));
        this.clusterName = clusterName;
    }

    public Client obtainClient() {
        return this.client;
    }

    @Override
    public void start() throws Exception {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        logger.debug("Settings used for connection to elasticsearch : {}", settings.toDelimitedString('#'));

        List<TransportAddress> addresses = hosts.stream()
                .map(ParseServerStringFunction::parse)
                .collect(toList());
        logger.debug("Hosts used for transport client : {}", addresses);

        this.client = new TransportClient(settings)
                .addTransportAddresses(addresses.toArray(new TransportAddress[addresses.size()]));

    }

    @Override
    public void stop() throws Exception {
        this.client.close();
    }
}
