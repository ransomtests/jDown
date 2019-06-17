package com.personal.projects.jdown.services;

import com.personal.projects.jdown.utils.CompletionTracker;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Downloader {
    private HttpClient httpClient;

    public Downloader() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Flowable<String> download(String url) {
        Path finalPath = Paths.get("D:/Workspace/IntelliJ/jdown/final");

        HttpRequest head = HttpRequest.newBuilder()
                                      .uri(URI.create(url))
                                      .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                      .build();
        long contentLength = 0L;

        try {
            contentLength = httpClient.sendAsync(head, HttpResponse.BodyHandlers.ofString())
                                      .thenApply(HttpResponse::headers)
                                      .thenApply(headers -> headers.firstValue("content-length")
                                                                   .orElse("0"))
                                      .thenApply(Long::parseLong)
                                      .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long n = contentLength;
        long part = contentLength / 10;

        List<Flowable<Path>> requests = new LinkedList<>();

        for (long index = 0, num = 0; num <= n; num = num + part + 1, index++) {
            String rangeHeader = String.format("bytes=%d-%d", num, num + part);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                                                 .uri(URI.create(url))
                                                 .header("Range", rangeHeader)
                                                 .GET()
                                                 .build();
            long completion = (index + 1) * 10;

            CompletableFuture<HttpResponse<Path>> futureRequest = httpClient.sendAsync(httpRequest,
                    HttpResponse.BodyHandlers.ofFile(Paths.get(String.format("D:/Workspace/IntelliJ/jdown/part%d", index))));

            Flowable<Path> finalRequest = Flowable.fromFuture(futureRequest)
                                                  .map(body -> {
                                                      CompletionTracker.incrementTracker(completion);
                                                      return body;
                                                  })
                                                  .map(HttpResponse::body);

            requests.add(finalRequest);

        }


        return Flowable.merge(requests)
                       .subscribeOn(Schedulers.single())
                       .map(path -> this.merge(path, finalPath));
    }


    private String merge(Path from, Path to) throws IOException {

        try (SeekableByteChannel part0 = Files.newByteChannel(from)) {
            try (SeekableByteChannel output = Files.newByteChannel(to, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                ByteBuffer buffer = ByteBuffer.allocate(2048);
                while (part0.read(buffer) > 0) {
                    buffer.flip();
                    output.write(buffer);
                    buffer.clear();
                }

                part0.close();
                buffer = buffer.clear();

                buffer.clear();
            }
        }

        return "done";
    }

}
