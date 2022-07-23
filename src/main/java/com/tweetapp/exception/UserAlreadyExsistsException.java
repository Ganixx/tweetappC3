package com.tweetapp.exception;

public class UserAlreadyExsistsException extends  Exception {
    private static final long serialVersionUID = 1L;

    public UserAlreadyExsistsException(String message) {
        super(message);
    }
}
    

