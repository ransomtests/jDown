package com.personal.projects.jdown;

import com.personal.projects.jdown.services.Downloader;
import com.personal.projects.jdown.services.Tracker;
import com.personal.projects.jdown.utils.CommandLineParser;
import com.personal.projects.jdown.utils.FileReader;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Application {
    public static void main(String[] args) throws IOException, InterruptedException {

        Map<String, String> parsedArguments = CommandLineParser.parse(args);
        String url = parsedArguments.get("url");
        String outputPath = parsedArguments.getOrDefault("outputDirectory", "");
        Path downloadDirectory = Paths.get(outputPath);
        downloadDirectory.toFile()
                         .mkdir();
        FileReader fileReader = FileReader.getInstance();
        Map<String, String> parsedFileExtensions = fileReader
                .getParsedFileExtensions("config/file-extensions.txt", ":");


        System.out.println("Initializing");
        URI uri = URI.create(url);
        Downloader downloader = new Downloader(parsedFileExtensions);
        long start = System.currentTimeMillis();
        Map<String, Object> downloadInfo = downloader.downloadInfo(uri);
        Long size = (Long) downloadInfo.get("size");
        String extension = (String) downloadInfo.get("extension");
        Path outputDirectory = fileReader.getFileDirectory("config/file-categorizations.txt", ":", downloadDirectory, extension,
                parsedArguments.containsKey("categorize"));
        outputDirectory.toFile()
                       .mkdir();


        System.out.println("Download started");
        String name = downloadInfo.get("name")
                                  .toString();
        Tracker.start(size, downloadDirectory, name)
               .subscribe(System.out::println, System.out::println);
        downloader.download(uri, downloadDirectory, size, name);
        System.out.println("Download complete. Merging!");
        Path targetFile = downloader.merge(downloadDirectory, outputDirectory, name);

        System.out.println(String.format("%nDownload Time -> %d", System.currentTimeMillis() - start));
        System.out.println(String.format("File path -> %s", targetFile.toAbsolutePath()
                                                                      .toString()));
    }

}
