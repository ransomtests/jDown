package com.personal.projects.jdown.services;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

public class MergeTest {
    @Test
    public void merge() {
        Path basePath = Paths.get("D:/Workspace/IntelliJ/jdown/downloads");

        Path finalFile = basePath.resolve("merged.pdf");

        IntStream.rangeClosed(0, 15)
                 .mapToObj(index -> String.format("part%d", index))
                 .map(basePath::resolve)
                 .forEach(path -> {
                     try {
                         this.merge(path, finalFile);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
    }

    private String merge(Path from, Path to) throws IOException {
        System.out.println(from);
        System.out.println(to);
        try (SeekableByteChannel part = Files.newByteChannel(from)) {
            try (SeekableByteChannel output = Files.newByteChannel(to, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                ByteBuffer buffer = ByteBuffer.allocate(100 * 1024);
                while (part.read(buffer) > 0) {
                    buffer.flip();
                    output.write(buffer);
                    buffer.clear();
                }
                buffer.clear();
//                Files.delete(from);
            }
        }

        return "done";
    }
}
