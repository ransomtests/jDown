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
        ComputationUtils.computePercentageDownloaded(downloadStatus);
        ComputationUtils.computeTimeLeft(downloadStatus);
        ComputationUtils.computeDownloadSpeed(downloadStatus);

        return downloadStatus;
    }

    public static Flowable<DownloadStatus> start(long size, Path baseDirectory, String name) {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .subscribeOn(Schedulers.single())
                       .map(timeElapsed -> new DownloadStatus(timeElapsed, Tracker.bytesDownloaded(baseDirectory, name), size))
                       .map(Tracker::updateTracker)
                       .takeWhile(downloadStatus -> downloadStatus.getPercentageDownloaded() <= 100);
    }

    private static long bytesDownloaded(Path baseDirectory, String name) {
        try (Stream<Path> directoryWalker = Files.walk(baseDirectory)) {

            return directoryWalker
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> file.getName()
                                        .contains(name))
                    .mapToLong(File::length)
                    .sum();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
