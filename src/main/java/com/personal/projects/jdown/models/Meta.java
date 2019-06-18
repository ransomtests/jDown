package com.personal.projects.jdown.models;

import lombok.Data;

@Data
public class Meta {
    private final long fileSize;
    private long bytesDownloaded;
    private long timeElapsed;
    private double timeLeft;
    private double percentageDownloaded;
    private String downloadString;
    private String downloadSpeed;

    public Meta(long timeElapsed, long bytesDownloaded, long fileSize) {
        this.bytesDownloaded = bytesDownloaded;
        this.timeElapsed = timeElapsed;
        this.fileSize = fileSize;
    }

}
