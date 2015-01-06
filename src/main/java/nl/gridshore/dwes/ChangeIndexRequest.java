package nl.gridshore.dwes;

import javax.validation.constraints.NotNull;

/**
 * Value object used to send a request for changing the index.
 */
public class ChangeIndexRequest {
    private int numReplicas;
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumReplicas() {
        return numReplicas;
    }

    public void setNumReplicas(int numReplicas) {
        this.numReplicas = numReplicas;
    }

    @Override
    public String toString() {
        return "ChangeIndexRequest{" +
                "name='" + name + '\'' +
                ", numReplicas=" + numReplicas +
                '}';
    }
}
