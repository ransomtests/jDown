package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.utils.CompletionTracker;
import com.personal.projects.jdown.utils.FileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {

            URI url = URI.create(args[0]);
            Downloader downloader = new Downloader(FileReader.getInstance()
                                                             .getParsedConfig("config/file-extensions.txt", ":"));
            String basePath;
            System.out.println("Enter base path for download");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                basePath = reader.readLine();
            }
            long start = System.currentTimeMillis();
            CompletionTracker.start()
                             .subscribe();

            downloader.download(url, Paths.get(basePath))
                      .blockingSubscribe(res ->{}, System.out::println);

            System.out.println(String.format("%nDownload Time -> %d", System.currentTimeMillis() - start));
        }
    }
}
