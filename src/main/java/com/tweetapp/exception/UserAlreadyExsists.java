package com.tweetapp.exception;

public class UserAlreadyExsists extends Exception {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExsists(String message) {
        super(message);
    }
}
