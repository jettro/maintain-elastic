package nl.gridshore.dwes.index.api;

/**
 * Value object to return information about the found indices.
 */
public class ElasticIndex {
    private String name;
    private String state;
    private int numberOfShards;
    private int numberOfReplicas;
    private long numberOfSegments;
    private String status;
    private long docCount;
    private String size;
    private String aliases;

    public ElasticIndex(String name) {
        this.name = name;
    }

    public ElasticIndex state(String state) {
        this.state = state;
        return this;
    }

    public ElasticIndex numberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
        return this;
    }

    public ElasticIndex numberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
        return this;
    }

    public ElasticIndex numberOfSegments(long numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        return this;
    }

    public ElasticIndex status(String status) {
        this.status = status;
        return this;
    }

    public ElasticIndex docCount(long count) {
        this.docCount = count;
        return this;
    }

    public ElasticIndex size(String sizeAsString) {
        this.size = sizeAsString;
        return this;
    }

    public ElasticIndex aliases(String aliases) {
        this.aliases = aliases;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public String getStatus() {
        return status;
    }

    public long getDocCount() {
        return docCount;
    }

    public String getSize() {
        return size;
    }

    public String getAliases() {
        return aliases;
    }

    public long getNumberOfSegments() {
        return numberOfSegments;
    }
}
