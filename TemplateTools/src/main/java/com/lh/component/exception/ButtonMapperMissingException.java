package com.lh.component.exception;

public class ButtonMapperMissingException extends RuntimeException {
    public ButtonMapperMissingException() {
    }

    public ButtonMapperMissingException(String s) {
        super(s);
    }

    public ButtonMapperMissingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ButtonMapperMissingException(Throwable throwable) {
        super(throwable);
    }

    public ButtonMapperMissingException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
