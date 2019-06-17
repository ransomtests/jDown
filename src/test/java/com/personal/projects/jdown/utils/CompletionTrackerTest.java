package com.personal.projects.jdown.utils;

import io.reactivex.Flowable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CompletionTrackerTest {
    @Test
    public void testTracker() throws InterruptedException {

        Flowable.interval(1, TimeUnit.SECONDS)
                .map(res -> CompletionTracker.displayTracker())
                .map(res -> {
                    CompletionTracker.incrementTracker(50);
                    return res;
                })
                .takeWhile(res -> CompletionTracker.getTrackerValue() < 100)
                .subscribe(System.out::println);

        Thread.sleep(10000);

    }
}
