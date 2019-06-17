package com.personal.projects.jdown.services;

import com.personal.projects.jdown.utils.CompletionTracker;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Downloader {
    private final int partitions;
    private HttpClient httpClient;
    private Map<String, String> fileTypes;


    public Downloader(Map<String, String> fileTypes) {
        this.httpClient = HttpClient.newHttpClient();
        this.fileTypes = fileTypes;
        this.partitions = Runtime.getRuntime()
                                 .availableProcessors() / 2;
    }

    public String download(URI url, Path basePath) {

        HttpRequest head = HttpRequest.newBuilder()
                                      .uri(url)
                                      .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                      .build();
        long contentLength = 0L;
        String extension = "";

        try {
            HttpResponse<String> response = httpClient.send(head, HttpResponse.BodyHandlers.ofString());
            HttpHeaders headers = response.headers();
            contentLength = headers
                    .firstValue("content-length")
                    .map(Long::parseLong)
                    .orElse(0L);

            extension = headers.firstValue("content-type")
                               .map(res -> fileTypes.getOrDefault(res, ""))
                               .orElse("");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }


        long part = contentLength / partitions;

        List<Flowable<Path>> requests = new LinkedList<>();

        ExecutorService executor = Executors.newFixedThreadPool(partitions);
        for (long index = 0, num = 0; index < partitions; num = num + part + 1, index++) {
            String rangeHeader = String.format("bytes=%d-%d", num, num + part);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                                                 .uri(url)
                                                 .header("Range", rangeHeader)
                                                 .GET()
                                                 .build();
            long completion = (long) ((index + 1) * 100.0 / partitions + 0.5);

            CompletableFuture<HttpResponse<Path>> futureRequest = httpClient.sendAsync(httpRequest,
                    HttpResponse.BodyHandlers.ofFile(basePath.resolve(String.format("part%d", index))));

            Flowable<Path> finalRequest = Flowable.fromFuture(futureRequest)
                                                  .subscribeOn(Schedulers.from(executor))
                                                  .map(body -> {
                                                      CompletionTracker.incrementTracker(completion);
                                                      return body;
                                                  })
                                                  .map(HttpResponse::body);

            requests.add(finalRequest);

        }

        Flowable.merge(requests)
                .blockingSubscribe(res -> {
                }, error -> {
                    error.printStackTrace();
                    System.out.println(error.getMessage());
                }, executor::shutdown);

        return extension;
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

    public void merge(Path baseDirectory, String extension) throws IOException {
        IntStream.range(0, partitions)
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
        Files.move(finalFile, baseDirectory.resolve(String.format("final%s", extension)));
    }
}
