package com.lh.kete.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Command {
    public static byte[] exec(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor(3, TimeUnit.SECONDS);
            if (p.exitValue() != 0) {
                return null;
            } else {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                IOUtils.copyThenClose(p.getInputStream(), result);
                return result.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
