package com.personal.projects.jdown.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileReaderTest {
    @Test
    public void readLinesTest() {
        FileReader.getInstance()
                  .getReader("config/file-extensions.txt")
                  .lines()
                  .forEach(System.out::println);
    }

    @Test
    public void parseConfigTest() {
        Map<String, String> parsedConfig = FileReader.getInstance()
                                                     .getParsedFileExtensions("config/file-extensions.txt", ":");
        System.out.println(parsedConfig.get("application/pdf"));
    }

    @Test
    public void getFileDirectoryTest() throws IOException {
        Path baseDirectory = Paths.get("D:/Workspace/IntelliJ/jdown/downloads");
        Path outputDirectory = FileReader.getInstance()
                                         .getFileDirectory("config/file-categorizations.txt", ":", baseDirectory, ".pdf");
        File directory = outputDirectory.toFile();
        directory.mkdir();
        Files.move(baseDirectory.resolve("file.pdf"), outputDirectory.resolve("file.pdf"));
        System.out.println(outputDirectory);
    }
}
