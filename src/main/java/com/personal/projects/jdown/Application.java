package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.utils.CompletionTracker;

public class Application {
    public static void main(String[] args) {
        String url = "https://stuff.mit.edu/afs/sipb/contrib/pi/pi-billion.txt";
        Downloader downloader = new Downloader();
        long start = System.currentTimeMillis();

        CompletionTracker.start()
                         .subscribe();

        downloader.download(url)
                  .blockingSubscribe(System.out::println);

        System.out.println(String.format("Execution Time -> %d", System.currentTimeMillis() - start));
    }
}
