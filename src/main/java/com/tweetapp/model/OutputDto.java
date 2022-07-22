package com.tweetapp.model;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDto<T> {
    private boolean error;
    private String errorMessage;
    private HttpStatus httpStatus;
    private boolean typeOfPage;
    private T data;
}
