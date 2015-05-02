package nl.gridshore.dwes.snapshot.api;

/**
 * Object to contain all parameters required to create a snapshot.
 */
public class CreateSnapshotRequest {
    private String name;
    private String prefix;
    private String repository;
    private String indexes;
    private Boolean ignoreUnavailable = true;
    private Boolean includeGlobalState = true;

    public Boolean getIgnoreUnavailable() {
        return ignoreUnavailable;
    }

    public void setIgnoreUnavailable(Boolean ignoreUnavailable) {
        this.ignoreUnavailable = ignoreUnavailable;
    }

    public Boolean getIncludeGlobalState() {
        return includeGlobalState;
    }

    public void setIncludeGlobalState(Boolean includeGlobalState) {
        this.includeGlobalState = includeGlobalState;
    }

    public String getIndexes() {
        return indexes;
    }

    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
}
