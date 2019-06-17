package com.personal.projects.jdown.services;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class DownloaderTest {

    @Test
    public void merge() throws IOException {

        SeekableByteChannel part0 = Files.newByteChannel(Paths.get("D:/Workspace/IntelliJ/jdown/part0"));
        SeekableByteChannel part1 = Files.newByteChannel(Paths.get("D:/Workspace/IntelliJ/jdown/part1"));
        Charset charset = Charset.forName("US-ASCII");
//        SeekableByteChannel output = Files.newByteChannel(Paths.get("D:/Workspace/IntelliJ/jdown/final"),
//                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        ByteBuffer buffer = ByteBuffer.allocate(100);
        while (part0.read(buffer) > 0) {
            buffer.flip();
            System.out.print(charset.decode(buffer));
            buffer.clear();
        }

        part0.close();
        buffer = buffer.clear();

        System.out.println();

        while (part1.read(buffer) > 0) {
            buffer.rewind();
            System.out.print(charset.decode(buffer));
            buffer.clear();
        }

        buffer.clear();
        part1.close();

    }

    @Test
    public void contentLengthTest() {
        long contentLength = 476070;
        long n = contentLength;
        int partitions = Runtime.getRuntime().availableProcessors()*2;
        long part = contentLength / partitions;

        for (long index = 0, num = 0; index<partitions; num = num + part + 1, index++) {
            String rangeHeader = String.format("bytes=%d-%d", num, num + part);
            long completion = (long) ((index+1) * 100.0 / partitions + 0.5);

            System.out.println(String.format("Range: %s%nCompletion: %d%n",rangeHeader,completion));

        }


        System.out.println(contentLength);
    }

    @Test
    public void flowableSubscriptionTest() {
        Flowable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(res -> {
                    System.out.println(Thread.currentThread().getName());
                    return "hello";
                })
                .blockingSubscribe(res -> System.out.println(Thread.currentThread().getName()));

    }

    @Test
    public void baseDirectoryTest() {
        Path path = Paths.get("D:/Workspace/IntelliJ/jdown/downloads/");
        path.resolve("final");

        System.out.println(path.resolve("final"));

    }

}
