package com.tweetapp.exception;

public class InvalidOtpException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidOtpException(String message) {
        super(message);
    }
}
