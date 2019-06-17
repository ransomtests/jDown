package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.utils.CompletionTracker;

import java.net.URI;

public class Application {
    public static void main(String[] args) {
        if (args.length > 0) {

            URI url = URI.create(args[0]);
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
}
