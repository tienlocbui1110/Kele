package com.lh.kete.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Command {
    public static String exec(String command) throws CommandException {
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor(3, TimeUnit.SECONDS);
            ByteArrayOutputStream errorbaos = new ByteArrayOutputStream();
            IOUtils.copyThenClose(p.getErrorStream(), errorbaos);
            if (p.exitValue() != 0) {
                throw new CommandException(p.exitValue(), errorbaos.toString());
            } else {
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                IOUtils.copyThenClose(p.getInputStream(), result);
                return (result.toString() + "\n" + errorbaos.toString());
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class CommandException extends Exception {
        private int errorCode;

        /**
         * Instantiates a new exception when exit code != 0
         */
        public CommandException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }
}
