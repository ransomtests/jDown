package com.personal.projects.jdown.utils;

import org.junit.Test;

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
                                                     .getParsedConfig("config/file-extensions.txt", ":");
        System.out.println(parsedConfig.get("application/pdf"));
    }
}
