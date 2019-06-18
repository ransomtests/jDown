package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.DownloadMeta;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class CompletionTracker {
    private static AtomicLong percentageCompletion = new AtomicLong(0);

    private static String updateTracker(DownloadMeta res, long size) {
        long percentageDownloaded = (long) ((res.getBytesDownloaded() * 100.0) / size + 0.5);
        percentageCompletion.set(percentageDownloaded);
        double timeLeft = (100.00 - percentageDownloaded) / percentageDownloaded * res.getTimeElapsed();

        return String.format("downloaded %d, elapsed %d, left %f", percentageDownloaded, res.getTimeElapsed(), timeLeft);
    }

    public static Flowable<String> start(long size, Path baseDirectory) {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .subscribeOn(Schedulers.single())
                       .map(timeElapsed -> new DownloadMeta(timeElapsed, CompletionTracker.bytesDownloaded(baseDirectory)))
                       .map(downloadMeta -> CompletionTracker.updateTracker(downloadMeta, size))
                       .takeWhile(res -> percentageCompletion.intValue() < 100);

    }

    private static long bytesDownloaded(Path baseDirectory) {
        try (Stream<Path> walker = Files.walk(baseDirectory)){

            return walker
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
