package com.personal.projects.jdown.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileReader {
    private static FileReader reader;

    public static FileReader getInstance() {
        if (reader == null) {
            synchronized (FileReader.class) {
                if (reader == null) {
                    reader = new FileReader();
                }
            }
        }
        return reader;
    }

    public BufferedReader getReader(String filePath) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass()
                                                                                   .getClassLoader()
                                                                                   .getResourceAsStream(filePath))));
    }

    public Map<String, String> getParsedFileExtensions(String filePath, String separator) {

        return this.getReader(filePath)
                   .lines()
                   .map(line -> line.split(separator))
                   .collect(HashMap::new, (map, elem) -> map.put(elem[0], elem[1]), HashMap::putAll);

    }

    public Path getFileDirectory(String filePath, String separator, Path baseDirectory, String extension) {

        return this.getReader(filePath)
                   .lines()
                   .filter(line -> line.contains(extension))
                   .findFirst()
                   .map(line -> line.split(separator)[0])
                   .map(baseDirectory::resolve)
                   .orElse(baseDirectory);
    }
}
