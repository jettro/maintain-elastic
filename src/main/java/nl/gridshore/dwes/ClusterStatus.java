package nl.gridshore.dwes;

/**
 * Created by jettrocoenradie on 23/12/14.
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
