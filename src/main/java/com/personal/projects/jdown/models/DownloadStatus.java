package com.personal.projects.jdown.models;

import lombok.Data;

@Data
public class DownloadStatus {
    private final long fileSize;
    private long bytesDownloaded;
    private long timeElapsed;
    private double timeLeft;
    private double percentageDownloaded;
    private String downloadSpeed;

    public DownloadStatus(long timeElapsed, long bytesDownloaded, long fileSize) {
        this.bytesDownloaded = bytesDownloaded;
        this.timeElapsed = timeElapsed;
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return String.format("completion %f, elapsed %d, left %f , bytes %d, downloadSpeed %s",
                percentageDownloaded,
                timeElapsed,
                timeLeft, bytesDownloaded, downloadSpeed);
    }
}
