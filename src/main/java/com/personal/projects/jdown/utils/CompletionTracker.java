package com.personal.projects.jdown.utils;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class CompletionTracker {
    private static AtomicLong percentage = new AtomicLong(0);

    static String displayTracker() {
        String stage = String.format("downloaded %d", percentage.intValue());
        StringBuilder output = IntStream.range(0, stage.length())
                                        .collect(StringBuilder::new, (builder, val) -> builder.append("\b"), StringBuilder::append);
        output.append(stage);
        System.out.print(output);
        return "";
    }

    public static void incrementTracker(long percentage) {
        CompletionTracker.percentage.set(percentage);
    }

    static int getTrackerValue() {
        return percentage.intValue();
    }

    public static Flowable<String> start() {

        return Flowable.interval(1, TimeUnit.SECONDS)
                       .map(res -> CompletionTracker.displayTracker())
                       .takeWhile(res -> percentage.intValue() < 100);

    }

}
