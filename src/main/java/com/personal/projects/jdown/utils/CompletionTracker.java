package com.personal.projects.jdown.utils;

import io.reactivex.Flowable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CompletionTracker {
    private static AtomicInteger percentage = new AtomicInteger(0);

    static String displayTracker() {
        System.out.print("tracker");
        return "";
    }

    public static void incrementTracker(int percentage) {
        CompletionTracker.percentage.accumulateAndGet(percentage, Integer::sum);
    }

    static int getTrackerValue() {
        return percentage.intValue();
    }

    public static Flowable<String> start() {

        return Flowable.interval(1, TimeUnit.MILLISECONDS)
                       .map(res -> CompletionTracker.displayTracker())
                       .takeWhile(res -> percentage.intValue() < 100);
    }
}
