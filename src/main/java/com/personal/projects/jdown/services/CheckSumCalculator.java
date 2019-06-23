package com.personal.projects.jdown.services;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSumCalculator {
    public static String checksum(Path file, String algorithm) throws IOException {
        return new DigestUtils(DigestUtils.getDigest(algorithm)).digestAsHex(Files.newInputStream(file));
    }

}
