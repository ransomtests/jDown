package com.personal.projects.jdown.utils;

import java.net.URI;
import java.net.http.HttpHeaders;
import java.util.Map;

public class DownloadUtils {

    static String fileName(HttpHeaders responseHeaders, URI url, Map<String, String> fileTypes, Map<String, Object> info) {
        String path = url.getPath();
        int lastIndexOfSlash = path
                .lastIndexOf("/");
        String fileName = path.substring(lastIndexOfSlash + 1);

        if ("".equals(fileName)) {
            String extension = fileExtension(responseHeaders, fileTypes, info);

            fileName = responseHeaders.firstValue("content-disposition")
                                      .map(header -> header.substring(header.indexOf("filename")))
                                      .map(name -> name.substring(name.indexOf("=")))
                                      .map(name -> name.substring(1))
                                      .map(name -> name.replaceAll("\"", ""))
                                      .orElse(String.format("final%s", extension));
        }
        info.put("name", fileName);
        return fileName;
    }

    static String fileExtension(HttpHeaders responseHeaders, Map<String, String> fileTypes, Map<String, Object> info) {
        String extension = responseHeaders.firstValue("content-type")
                                          .map(res -> fileTypes.getOrDefault(res, ""))
                                          .orElse("");

        info.putIfAbsent("extension", extension);
        return extension;
    }

    public static void populateFileInfo(HttpHeaders headers, URI url, Map<String, String> fileTypes, Map<String, Object> info) {
        contentLength(headers, info);
        fileExtension(headers, fileTypes, info);
        fileName(headers, url, fileTypes, info);
    }

    static Long contentLength(HttpHeaders headers, Map<String, Object> info) {
        long contentLength = headers
                .firstValueAsLong("content-length")
                .orElse(0L);
        info.put("size", contentLength);
        return contentLength;
    }
}
