package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.DownloadMeta;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

public class CompletionTrackerTest {

    @Test
    public void testTracker() throws InterruptedException {

        CompletionTracker.start(56L, Paths.get("D:/todelete"))
                         .subscribe(System.out::println, System.out::println);

        Thread.sleep(20000);
    }

    @Test
    public void sizeOfAllFiles() throws IOException {
        Path baseDirectory = Paths.get("D:/todelete");
        long size = Files.walk(baseDirectory)
                         .map(Path::toFile)
                         .filter(File::isFile)
                         .filter(file -> file.getName()
                                             .contains("part"))
                         .mapToLong(File::length)
                         .sum();
        System.out.println(size);
    }

    @Test
    public void createTestFiles() {
        Path baseDir = Paths.get("D:/todelete");
        long bytesWritten = IntStream.rangeClosed(0, 10)
                                     .mapToObj(index -> baseDir.resolve(String.format("part%d", index)))
                                     .mapToLong(path -> {
                                         long size = 0L;
                                         try {
                                             SeekableByteChannel seekableByteChannel = Files.newByteChannel(path,
                                                     StandardOpenOption.CREATE,
                                                     StandardOpenOption.APPEND);

                                             ByteBuffer buffer = ByteBuffer.allocate(100);
                                             String message = "hello";
                                             buffer.put(message.getBytes());
                                             buffer.flip();
                                             seekableByteChannel.write(buffer);
                                             buffer.clear();
                                             size = seekableByteChannel.size();
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }
                                         return size;
                                     })
                                     .sum();

        System.out.println(bytesWritten);
    }

    @Test
    public void timeLeft() {
        DownloadMeta res = new DownloadMeta(2, 100);
        long size = 300;
        long percentageDownloaded = (long) ((res.getBytesDownloaded() * 100.0) / size + 0.5);
        long timeLeft = (100 - percentageDownloaded) / percentageDownloaded * res.getTimeElapsed();

        String downloaded = String.format("downloaded %d, elapsed %d, left %d", res.getBytesDownloaded(), res.getTimeElapsed(),
                timeLeft);

        System.out.println(downloaded);
    }
}
