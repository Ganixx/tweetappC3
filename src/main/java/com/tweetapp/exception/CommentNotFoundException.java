package com.tweetapp.exception;

public class CommentNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public CommentNotFoundException(String message) {
        super(message);
    }
}
