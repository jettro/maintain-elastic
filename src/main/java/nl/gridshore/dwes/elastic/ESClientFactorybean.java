package nl.gridshore.dwes.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Elasticsearch client factory bean that returns a client instance. You are responsible for closing the client when
 * you are done with it. Client objects are expensive to use and should be reused within your application.
 * <p/>
 * The host string most be of format "host1:port1,host2:port2"
 * The cluster name must be the name of the cluster than runs on the provided host(s)
 */
public class ESClientFactorybean {
    private static final Logger logger = LoggerFactory.getLogger(ESClientFactorybean.class);
    private static final int DEFAULT_ELASITCSEARCH_PORT = 9300;

    public static Client obtainClient(String host, String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();

        logger.debug("Settings used for connection to elasticsearch : {}", settings.toDelimitedString('#'));

        TransportAddress[] addresses = getTransportAddresses(host);

        logger.debug("Hosts used for transport client : {}", (Object) addresses);

        return new TransportClient(settings).addTransportAddresses(addresses);

    }

    private static TransportAddress[] getTransportAddresses(String unicastHosts) {
        List<TransportAddress> transportAddresses = newArrayList();

        for (String unicastHost : unicastHosts.split(",")) {
            int port = DEFAULT_ELASITCSEARCH_PORT;
            String serverName = unicastHost;
            if (unicastHost.contains(":")) {
                String[] splitted = unicastHost.split(":");
                serverName = splitted[0];
                port = Integer.parseInt(splitted[1].trim());
            }
            transportAddresses.add(new InetSocketTransportAddress(serverName.trim(), port));
        }

        return transportAddresses.toArray(new TransportAddress[transportAddresses.size()]);
    }

}
