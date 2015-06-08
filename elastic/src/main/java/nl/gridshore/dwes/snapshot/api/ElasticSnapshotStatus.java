package nl.gridshore.dwes.snapshot.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Value object to contain information about the status of a running snapshot creation.
 */
public class ElasticSnapshotStatus {
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
    private String repository;
    private String snapshot;
    private int numberOfFiles;
    private int numberOfProcessedFiles;
    private long totalSize;
    private long processedSize;
    private String startTime;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public int getNumberOfProcessedFiles() {
        return numberOfProcessedFiles;
    }

    public void setNumberOfProcessedFiles(int numberOfProcessedFiles) {
        this.numberOfProcessedFiles = numberOfProcessedFiles;
    }

    public long getProcessedSize() {
        return processedSize;
    }

    public void setProcessedSize(long processedSize) {
        this.processedSize = processedSize;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void startTime(long epochSeconds) {
        this.startTime = convertFromEpochSeconds(epochSeconds);;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    private String convertFromEpochSeconds(long epochSeconds) {
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC).format(DATE_FORMATTER);
    }
}
