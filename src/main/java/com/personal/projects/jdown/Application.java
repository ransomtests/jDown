package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;

public class Application {
    public static void main(String[] args) {
        String url = "";
        Downloader downloader = new Downloader();
        long start = System.currentTimeMillis();

        downloader.mockDownloader(url)
                  .blockingSubscribe(System.out::println);

        System.out.println(System.currentTimeMillis() - start);
    }
}
