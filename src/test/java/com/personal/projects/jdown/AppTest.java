package com.personal.projects.jdown;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTest {
    @Test
    public void testPath() {
        Path download = Paths.get("D:\\Workspace\\toDelete");
        Path resolve = download.resolve("aa.txt");

        long length = resolve.toFile()
                             .length();

        System.out.println(length);


    }
}
