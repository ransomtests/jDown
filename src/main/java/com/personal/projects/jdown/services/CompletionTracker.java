package com.personal.projects.jdown.services;

import com.personal.projects.jdown.models.Meta;
import com.personal.projects.jdown.utils.ComputationUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CompletionTracker {

    private static Meta updateTracker(Meta meta) {
        double percentageDownloaded = ComputationUtils.computePercentageDownloaded(meta);
        double timeLeft = ComputationUtils.computeTimeLeft(meta);
        String speed = ComputationUtils.computeDownloadSpeed(meta);

        String downloadString = String.format("completion %f, elapsed %d, left %f , bytes %d, downloadSpeed %s",
                percentageDownloaded,
                meta.getTimeElapsed(),
                timeLeft, meta.getBytesDownloaded(), speed);

        meta.setDownloadString(downloadString);

        return meta;
    }

    public static Flowable<String> start(long size, Path baseDirectory) {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .subscribeOn(Schedulers.single())
                       .map(timeElapsed -> new Meta(timeElapsed, CompletionTracker.bytesDownloaded(baseDirectory), size))
                       .map(CompletionTracker::updateTracker)
                       .takeWhile(meta -> meta.getPercentageDownloaded() <= 100)
                       .map(Meta::getDownloadString);


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
