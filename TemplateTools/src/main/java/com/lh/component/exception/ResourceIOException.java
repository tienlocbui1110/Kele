package com.lh.component.exception;

public class ResourceIOException extends RuntimeException {
    public ResourceIOException() {
    }

    public ResourceIOException(String s) {
        super(s);
    }

    public ResourceIOException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ResourceIOException(Throwable throwable) {
        super(throwable);
    }

    public ResourceIOException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
