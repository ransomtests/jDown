package com.personal.projects.jdown.services;

import com.personal.projects.jdown.models.DownloadStatus;
import com.personal.projects.jdown.utils.ComputationUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Tracker {

    private static DownloadStatus updateTracker(DownloadStatus downloadStatus) {
        double percentageDownloaded = ComputationUtils.computePercentageDownloaded(downloadStatus);
        double timeLeft = ComputationUtils.computeTimeLeft(downloadStatus);
        String speed = ComputationUtils.computeDownloadSpeed(downloadStatus);

        String downloadString = String.format("completion %f, elapsed %d, left %f , bytes %d, downloadSpeed %s",
                percentageDownloaded,
                downloadStatus.getTimeElapsed(),
                timeLeft, downloadStatus.getBytesDownloaded(), speed);

        downloadStatus.setDownloadString(downloadString);

        return downloadStatus;
    }

    public static Flowable<String> start(long size, Path baseDirectory) {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .subscribeOn(Schedulers.single())
                       .map(timeElapsed -> new DownloadStatus(timeElapsed, Tracker.bytesDownloaded(baseDirectory), size))
                       .map(Tracker::updateTracker)
                       .takeWhile(downloadStatus -> downloadStatus.getPercentageDownloaded() <= 100)
                       .map(DownloadStatus::getDownloadString);

    }

    private static long bytesDownloaded(Path baseDirectory) {
        try (Stream<Path> directoryWalker = Files.walk(baseDirectory)) {

            return directoryWalker
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName()
                                        .contains("part"))
                    .mapToLong(File::length)
                    .sum();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
