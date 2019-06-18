package com.lh.component.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String s) {
        super(s);
    }

    public ResourceNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
