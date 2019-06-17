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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Downloader {
    private HttpClient httpClient;
    private Map<String, String> fileTypes;

    public Downloader() {
        this.httpClient = HttpClient.newHttpClient();
        fileTypes = new HashMap<>();
        fileTypes.put("application/pdf", ".pdf");
        fileTypes.put("text/plain", ".txt");
    }

    public Flowable<String> download(String url) {

        HttpRequest head = HttpRequest.newBuilder()
                                      .uri(URI.create(url))
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

        Path outputFile = Paths.get(String.format("D:/Workspace/IntelliJ/jdown/final%s", extension));

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
                       .map(path -> this.merge(path, outputFile));
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
