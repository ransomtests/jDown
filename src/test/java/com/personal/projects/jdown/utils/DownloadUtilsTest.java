package com.personal.projects.jdown.utils;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;

public class DownloadUtilsTest {

    @Test
    public void fallbackFileName() {
        HashMap<String, List<String>> headers = new HashMap<>();
        URI url = URI.create("http://www.aditapillai.com");
        HashMap<String, String> fileTypes = new HashMap<>();
        fileTypes.put("application/pdf", ".pdf");
        headers.put("content-type", List.of("application/pdf"));
        String fileName = DownloadUtils.fileName(HttpHeaders.of(headers, (s, s2) -> true), url, fileTypes, new HashMap<>());
        Assert.assertEquals("final.pdf", fileName);
    }

    @Test
    public void fileNameFromContentDisposition() {
        HashMap<String, List<String>> headers = new HashMap<>();
        URI url = URI.create("http://www.aditapillai.com");
        HashMap<String, String> fileTypes = new HashMap<>();
        fileTypes.put("application/pdf", ".pdf");
        headers.put("content-type", List.of("application/pdf"));
        headers.put("Content-Disposition", List.of("attachment; filename=\"application.jpg\""));
        String fileName = DownloadUtils.fileName(HttpHeaders.of(headers, (s, s2) -> true), url, fileTypes, new HashMap<>());
        Assert.assertEquals("application.jpg", fileName);
    }

    @Test
    public void fileNameFromUrl() {
        HashMap<String, List<String>> headers = new HashMap<>();
        URI url = URI.create("http://www.aditapillai.com/resume.pdf");
        HashMap<String, String> fileTypes = new HashMap<>();
        fileTypes.put("application/pdf", ".pdf");
        headers.put("content-type", List.of("application/pdf"));
        headers.put("Content-Disposition", List.of("attachment; filename=\"application.jpg\""));
        String fileName = DownloadUtils.fileName(HttpHeaders.of(headers, (s, s2) -> true), url, fileTypes, new HashMap<>());
        Assert.assertEquals("resume.pdf", fileName);
    }


}
