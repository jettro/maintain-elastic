package nl.gridshore.dwes.index.api;

/**
 * Value object for passing a request to optimize an index.
 */
public class OptimizeIndexRequest {
    private String name;
    private int maxSegments;

    public OptimizeIndexRequest(String name, int maxSegments) {
        this.name = name;
        this.maxSegments = maxSegments;
    }

    public OptimizeIndexRequest() {
    }

    public int getMaxSegments() {
        return maxSegments;
    }

    public void setMaxSegments(int maxSegments) {
        this.maxSegments = maxSegments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OptimizeIndexRequest{" +
                "maxSegments=" + maxSegments +
                ", name='" + name + '\'' +
                '}';
    }
}
