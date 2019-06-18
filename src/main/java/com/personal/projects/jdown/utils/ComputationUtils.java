package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.Meta;

public class ComputationUtils {

    private static final double ONE_MB = 1024.0 * 1024;

    public static String computeDownloadSpeed(Meta meta) {
        double speed = meta.getBytesDownloaded() / ONE_MB / meta.getTimeElapsed();
        speed = isValid(speed) ? speed : 0.0;
        String downloadSpeed = String.format("%f MB/s", speed);
        meta.setDownloadSpeed(downloadSpeed);
        return downloadSpeed;
    }

    public static double computePercentageDownloaded(Meta res) {
        long size = res.getFileSize();
        double percentageDownloaded = ((res.getBytesDownloaded() * 100.0) / size);
        percentageDownloaded = isValid(percentageDownloaded) ? percentageDownloaded : 0.0;
        res.setPercentageDownloaded(percentageDownloaded);
        return percentageDownloaded;
    }

    public static double computeTimeLeft(Meta res) {
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
