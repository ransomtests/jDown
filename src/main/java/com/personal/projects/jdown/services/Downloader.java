package com.personal.projects.jdown.services;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class Downloader {
    public Flowable<String> mockDownloader(String url) {

        Flowable<String> first = Flowable.fromCallable(this::getName)
                                         .subscribeOn(Schedulers.io());
        Flowable<String> second = Flowable.fromCallable(this::getName).subscribeOn(Schedulers.io());
        Flowable<String> third = Flowable.fromCallable(this::getName).subscribeOn(Schedulers.io());

        return Flowable.zip(first, second, third, (f, s, t) -> String.format("%s\n%s\n%s\n", f, s, t));
    }

    private String getName() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.format("Adit %d", System.nanoTime());
    }
}
