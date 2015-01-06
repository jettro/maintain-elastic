package nl.gridshore.dwes;

/**
 * Value object to send the status of the cluster
 */
public class ClusterStatus {
    private String status;
    private String name;

    public ClusterStatus(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
