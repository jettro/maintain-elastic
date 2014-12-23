package nl.gridshore.dwes.elastic;

import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Strategy for obtaining an {@link org.elasticsearch.common.transport.InetSocketTransportAddress} from a String containing only a server name or a
 * combination of servername and port in the format of servername:port.
 * <p>
 * If no port is provide in the serverString, we use the default port {@value #DEFAULT_ELASITCSEARCH_PORT}
 */
public class ParseServerStringFunction {
    static final int DEFAULT_ELASITCSEARCH_PORT = 9300;

    public static InetSocketTransportAddress parse(String serverConfig) {
        int port = DEFAULT_ELASITCSEARCH_PORT;
        String serverName = serverConfig;
        if (serverConfig.contains(":")) {
            String[] splitted = serverConfig.split(":");
            serverName = splitted[0];
            port = Integer.parseInt(splitted[1].trim());
        }
        return new InetSocketTransportAddress(serverName, port);
    }
}
