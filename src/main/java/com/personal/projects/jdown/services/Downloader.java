package com.personal.projects.jdown.services;

import com.personal.projects.jdown.utils.DownloadUtils;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Downloader {
    private int availablePartitions;
    private HttpClient httpClient;
    private Map<String, String> fileTypes;


    public Downloader(Map<String, String> fileTypes) {
        this.httpClient = HttpClient.newHttpClient();
        this.fileTypes = fileTypes;
        int partitions = Runtime.getRuntime()
                                .availableProcessors() / 2;
        this.availablePartitions = partitions == 0 ? 1 : partitions;
    }

    public Map<String, Object> downloadInfo(URI url) throws IOException, InterruptedException {
        HttpRequest head = HttpRequest.newBuilder()
                                      .uri(url)
                                      .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                      .build();
        HttpResponse<String> response = httpClient.send(head, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();
        HashMap<String, Object> info = new HashMap<>();
        DownloadUtils.populateFileInfo(headers, url, fileTypes, info);
        return info;

    }

    public void download(URI url, Path basePath, long contentLength) {

        long part = contentLength / availablePartitions;

        List<Flowable<Path>> requests = new LinkedList<>();

        ExecutorService executor = Executors.newFixedThreadPool(availablePartitions);
        for (long index = 0, num = 0; index < availablePartitions; num = num + part + 1, index++) {
            String rangeHeader = String.format("bytes=%d-%d", num, num + part);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                                                 .uri(url)
                                                 .header("Range", rangeHeader)
                                                 .GET()
                                                 .build();

            CompletableFuture<HttpResponse<Path>> futureRequest = httpClient.sendAsync(httpRequest,
                    HttpResponse.BodyHandlers.ofFile(basePath.resolve(String.format("part%d", index))));

            Flowable<Path> finalRequest = Flowable.fromFuture(futureRequest)
                                                  .subscribeOn(Schedulers.from(executor))
                                                  .map(HttpResponse::body);

            requests.add(finalRequest);

        }

        Flowable.merge(requests)
                .blockingSubscribe(res -> {
                }, error -> {
                    error.printStackTrace();
                    System.out.println(error.getMessage());
                }, executor::shutdown);

    }


    private void merge(Path from, Path baseDirectory) throws IOException {
        Path to = baseDirectory.resolve("final");
        try (SeekableByteChannel part = Files.newByteChannel(from)) {
            try (SeekableByteChannel output = Files.newByteChannel(to, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                ByteBuffer buffer = ByteBuffer.allocate(100 * 1024);
                while (part.read(buffer) > 0) {
                    buffer.flip();
                    output.write(buffer);
                    buffer.clear();
                }
                buffer.clear();
                Files.delete(from);
            }
        }
    }

    public void merge(Path baseDirectory, Path outputDirectory, String name) throws IOException {
        IntStream.range(0, availablePartitions)
                 .mapToObj(index -> String.format("part%d", index))
                 .map(baseDirectory::resolve)
                 .forEach(path -> {
                     try {
                         this.merge(path, baseDirectory);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });

        Path finalFile = baseDirectory.resolve("final");
        outputDirectory.toFile()
                       .mkdir();
        Files.move(finalFile, outputDirectory.resolve(name));
    }
}
