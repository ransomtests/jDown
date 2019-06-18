package com.personal.projects.jdown.utils;

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

    private static String downloadStage() {
        String stage = String.format("downloaded %d", percentageCompletion.intValue());
//        StringBuilder output = IntStream.range(0, stage.length())
//                                        .collect(StringBuilder::new, (builder, val) -> builder.append("\b"),
//                                        StringBuilder::append);
//        output.append(stage);
//        return output.toString();
        return stage;
    }

    private static long updateTracker(long res, long size) {
        long percentageDownloaded = (long) ((res * 100.0) / size + 0.5);
        percentageCompletion.set(percentageDownloaded);
        return percentageCompletion.longValue();
    }

    public static Flowable<String> start(long size, Path baseDirectory) {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .subscribeOn(Schedulers.io())
                       .map(res -> CompletionTracker.bytesDownloaded(baseDirectory))
                       .map(res -> CompletionTracker.updateTracker(res, size))
                       .map(res -> CompletionTracker.downloadStage())
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
