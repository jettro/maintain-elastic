package nl.gridshore.dwes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.Files;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * Configuration class for the application.
 */
public class DWESConfiguration extends Configuration {
    @NotEmpty
    private String elasticsearchHost = "localhost:9300";

    @NotEmpty
    private String clusterName = "elasticsearch";

    private String tempUploadFolder = Files.createTempDir().getAbsolutePath();

    private String usernamePassword = "";

    @JsonProperty
    public String getElasticsearchHost() {
        return elasticsearchHost;
    }

    @JsonProperty
    public void setElasticsearchHost(String elasticsearchHost) {
        this.elasticsearchHost = elasticsearchHost;
    }

    @JsonProperty
    public String getClusterName() {
        return clusterName;
    }

    @JsonProperty
    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTempUploadFolder() {
        return tempUploadFolder;
    }

    @JsonProperty
    public String getUsernamePassword() {
        return usernamePassword;
    }

    @JsonProperty
    public void setUsernamePassword(String usernamePassword) {
        this.usernamePassword = usernamePassword;
    }
}
