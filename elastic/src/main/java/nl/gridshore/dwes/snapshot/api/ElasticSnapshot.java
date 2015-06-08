package nl.gridshore.dwes.snapshot.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * Value object to send information about snapshots.
 */
public class ElasticSnapshot {
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
    private String name;
    private String repository;
    private String startTime;
    private String endTime;
    private String state;
    private String status;
    private List<String> indexes;
    private List<ElasticSnapshotError> errors;

    public ElasticSnapshot(String name) {
        this.name = name;
        this.indexes = new ArrayList<>();
    }

    /* handy methods */
    public void startTime(long epochSeconds) {
        this.startTime = convertFromEpochSeconds(epochSeconds);
    }

    public void endTime(long epochSeconds) {
        this.endTime = convertFromEpochSeconds(epochSeconds);
    }

    public void addIndex(String name) {
        indexes.add(name);
    }

    public void addError(String index, String message) {
        if (errors == null) {
            this.errors = new ArrayList<>();
        }
        errors.add(new ElasticSnapshotError(index, message));
    }

    /* getter and setters */
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<String> indexes) {
        this.indexes = indexes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<ElasticSnapshotError> getErrors() {
        return errors;
    }

    public void setErrors(List<ElasticSnapshotError> errors) {
        this.errors = errors;
    }

    private String convertFromEpochSeconds(long epochSeconds) {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC).format(DATE_FORMATTER);
    }

    public class ElasticSnapshotError {
        private String message;
        private String index;

        public ElasticSnapshotError(String index, String message) {
            this.index = index;
            this.message = message;
        }

        public String getIndex() {
            return index;
        }

        public String getMessage() {
            return message;
        }
    }
}
