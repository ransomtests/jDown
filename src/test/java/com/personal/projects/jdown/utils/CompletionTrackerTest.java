package com.personal.projects.jdown.utils;

import io.reactivex.Flowable;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CompletionTrackerTest {
    @Test
    public void testTracker() throws InterruptedException {

        Flowable.interval(100, TimeUnit.MILLISECONDS)
                .map(res -> CompletionTracker.displayTracker())
                .map(res -> {
                    CompletionTracker.incrementTracker(1);
                    return res;
                })
                .takeWhile(res -> CompletionTracker.getTrackerValue() < 110)
                .subscribe(System.out::println);

        Thread.sleep(20000);

    }
}
