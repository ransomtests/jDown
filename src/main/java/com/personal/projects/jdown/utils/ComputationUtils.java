package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.DownloadStatus;

public class ComputationUtils {

    private static final double ONE_MB = 1024.0 * 1024;

    public static String computeDownloadSpeed(DownloadStatus downloadStatus) {
        double speed = downloadStatus.getBytesDownloaded() / ONE_MB / downloadStatus.getTimeElapsed();
        speed = isValid(speed) ? speed : 0.0;
        String downloadSpeed = String.format("%f MB/s", speed);
        downloadStatus.setDownloadSpeed(downloadSpeed);
        return downloadSpeed;
    }

    public static double computePercentageDownloaded(DownloadStatus res) {
        long size = res.getFileSize();
        double percentageDownloaded = ((res.getBytesDownloaded() * 100.0) / size);
        percentageDownloaded = isValid(percentageDownloaded) ? percentageDownloaded : 0.0;
        res.setPercentageDownloaded(percentageDownloaded);
        return percentageDownloaded;
    }

    public static double computeTimeLeft(DownloadStatus res) {
        double percentageDownloaded = res.getPercentageDownloaded();
        double timeLeft = (100.00 - percentageDownloaded) / percentageDownloaded * res.getTimeElapsed();
        timeLeft = isValid(timeLeft) ? timeLeft : 0.0;
        res.setTimeLeft(timeLeft);
        return timeLeft;
    }

    private static boolean isValid(double value) {
        return !(Double.isNaN(value) || Double.isInfinite(value));
    }
}
