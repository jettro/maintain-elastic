package nl.gridshore.dwes;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class Index {
    private String name;

    public Index() {
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }
}
