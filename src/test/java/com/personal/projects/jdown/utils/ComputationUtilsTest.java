package com.personal.projects.jdown.utils;

import com.personal.projects.jdown.models.Meta;
import org.junit.Test;

public class ComputationUtilsTest {
    @Test
    public void testDownloadSpeed() {
        Meta meta = new Meta(3000, 343606400, 1000000002);
        System.out.println(ComputationUtils.computeDownloadSpeed(meta));
    }
}
