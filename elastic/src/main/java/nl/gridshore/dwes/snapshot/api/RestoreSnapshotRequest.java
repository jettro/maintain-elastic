package nl.gridshore.dwes.snapshot.api;

/**
 * Request object for restoring a snapshot
 */
public class RestoreSnapshotRequest {
    private String repository;
    private String snapshot;
    private String indexes;
    private String renamePattern;
    private String renameReplacement;
    private boolean ignoreUnavailable;
    private boolean includeGlobalState;
    private boolean includeAliases;

    public boolean isIgnoreUnavailable() {
        return ignoreUnavailable;
    }

    public void setIgnoreUnavailable(boolean ignoreUnavailable) {
        this.ignoreUnavailable = ignoreUnavailable;
    }

    public boolean isIncludeAliases() {
        return includeAliases;
    }

    public void setIncludeAliases(boolean includeAliases) {
        this.includeAliases = includeAliases;
    }

    public boolean isIncludeGlobalState() {
        return includeGlobalState;
    }

    public void setIncludeGlobalState(boolean includeGlobalState) {
        this.includeGlobalState = includeGlobalState;
    }

    public String getIndexes() {
        return indexes;
    }

    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }

    public String getRenamePattern() {
        return renamePattern;
    }

    public void setRenamePattern(String renamePattern) {
        this.renamePattern = renamePattern;
    }

    public String getRenameReplacement() {
        return renameReplacement;
    }

    public void setRenameReplacement(String renameReplacement) {
        this.renameReplacement = renameReplacement;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }
}
