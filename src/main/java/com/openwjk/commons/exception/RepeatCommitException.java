package com.openwjk.commons.exception;

public class RepeatCommitException extends RuntimeException {
    public RepeatCommitException(String message) {
        super(message);
    }

    public RepeatCommitException(String message, Throwable cause) {
        super(message, cause);
    }

}
