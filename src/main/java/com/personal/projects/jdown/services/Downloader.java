package com.personal.projects.jdown.services;

import com.personal.projects.jdown.utils.CompletionTracker;
import io.reactivex.Flowable;

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
import java.util.concurrent.CompletableFuture;

public class Downloader {
    private HttpClient httpClient;

    public Downloader() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public Flowable<String> download(String url) {
        Path path = Paths.get("D:/Workspace/IntelliJ/jdown/final");

        HttpRequest downloadRequest = HttpRequest.newBuilder()
                                                 .uri(URI.create(url))
                                                 .header("Range", "bytes=0-500")
                                                 .GET()
                                                 .build();
        HttpRequest downloadRequest2 = HttpRequest.newBuilder()
                                                  .uri(URI.create(url))
                                                  .header("Range", "bytes=501-1000")
                                                  .GET()
                                                  .build();

        CompletableFuture<HttpResponse<Path>> response = httpClient.sendAsync(downloadRequest,
                HttpResponse.BodyHandlers.ofFile(Paths.get("D:/Workspace/IntelliJ/jdown/part0")));

        CompletableFuture<HttpResponse<Path>> response2 = httpClient.sendAsync(downloadRequest2,
                HttpResponse.BodyHandlers.ofFile(Paths.get("D:/Workspace/IntelliJ/jdown/part1")));


        Flowable<Path> res = Flowable.fromFuture(response)
                                     .map(body -> {
                                         CompletionTracker.incrementTracker(50);
                                         return body;
                                     })
                                     .map(HttpResponse::body);

        Flowable<Path> res2 = Flowable.fromFuture(response2)
                                      .map(body -> {
                                          CompletionTracker.incrementTracker(50);
                                          return body;
                                      })
                                      .map(HttpResponse::body);


        return Flowable.zip(res, res2, (r, r2) -> {

            SeekableByteChannel part0 = Files.newByteChannel(r);
            SeekableByteChannel part1 = Files.newByteChannel(r2);
            SeekableByteChannel output = Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            ByteBuffer buffer = ByteBuffer.allocate(100);
            while (part0.read(buffer) > 0) {
                buffer.flip();
                output.write(buffer);
                buffer.clear();
            }

            part0.close();
            buffer = buffer.clear();

            while (part1.read(buffer) > 0) {
                buffer.rewind();
                output.write(buffer);
                buffer.clear();
            }

            buffer.clear();
            output.close();
            part1.close();

            return "done";
        });
    }

}
