package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.utils.CompletionTracker;
import com.personal.projects.jdown.utils.FileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {
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
            Map<String, Object> fileMeta = downloader.fileMeta(url);
            Long size = (Long) fileMeta.get("size");
            Path baseDirectory = Paths.get(basePath);

            CompletionTracker.start(size, baseDirectory)
                             .subscribe(System.out::println, System.out::println);

            downloader.download(url, baseDirectory, size);
            System.out.println("Download complete. Merging!");
            downloader.merge(baseDirectory, (String) fileMeta.get("extension"));

            System.out.println(String.format("%nDownload Time -> %d", System.currentTimeMillis() - start));
        } else {
            System.out.println("Could not download");
        }
    }
}
