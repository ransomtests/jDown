package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.DownloadStatus;
import org.junit.Test;

public class ComputationUtilsTest {
    @Test
    public void testDownloadSpeed() {
        DownloadStatus downloadStatus = new DownloadStatus(3000, 343606400, 1000000002);
        System.out.println(ComputationUtils.computeDownloadSpeed(downloadStatus));
    }
}
