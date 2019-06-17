package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.utils.CompletionTracker;

public class Application {
    public static void main(String[] args) {
        String url = "http://www.osborne.cc/subs/Sub%20Questionnaire.pdf";
        Downloader downloader = new Downloader();
        long start = System.currentTimeMillis();

        CompletionTracker.start()
                         .subscribe();

        downloader.download(url)
                  .blockingSubscribe(success -> {
                  }, System.out::println);

        System.out.println(String.format("%nExecution Time -> %d", System.currentTimeMillis() - start));
    }
}
