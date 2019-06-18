package com.personal.projects.jdown.models;

public class DownloadMeta {
    private long bytesDownloaded;
    private long timeElapsed;

    public DownloadMeta(long timeElapsed, long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
        this.timeElapsed = timeElapsed;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

}
