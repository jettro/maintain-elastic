package nl.gridshore.dwes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by jettrocoenradie on 05/01/15.
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
