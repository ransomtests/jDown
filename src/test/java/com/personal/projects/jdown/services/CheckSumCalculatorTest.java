package com.personal.projects.jdown.services;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class CheckSumCalculatorTest {

    @Test
    public void checksum() throws IOException, NoSuchAlgorithmException {
        String hash = CheckSumCalculator.checksum(Paths.get("D:\\Workspace\\IntelliJ\\jdown\\pi-billion.txt"), "sha-256");
        System.out.println(hash);
    }
}