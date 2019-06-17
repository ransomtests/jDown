package com.personal.projects.jdown.utils;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class CompletionTracker {
    private static AtomicInteger percentage = new AtomicInteger(0);

    static String displayTracker() {
        String stage = String.format("downloaded %d", percentage.intValue());
        StringBuilder output = IntStream.range(0, stage.length())
                                        .collect(StringBuilder::new, (builder, val) -> builder.append("\b"),
                                                StringBuilder::append);
        output.append(stage);
        System.out.print(output);
//        System.out.println(String.format("downloaded %d", percentage.intValue()));
        return "";
    }

    public static void incrementTracker(int percentage) {
        CompletionTracker.percentage.accumulateAndGet(percentage, Integer::sum);
    }

    static int getTrackerValue() {
        return percentage.intValue();
    }

    public static Flowable<String> start() {

        return Flowable.interval(100, TimeUnit.MILLISECONDS)
                       .map(res -> CompletionTracker.displayTracker())
                       .takeWhile(res -> percentage.intValue() < 100);

    }

}
