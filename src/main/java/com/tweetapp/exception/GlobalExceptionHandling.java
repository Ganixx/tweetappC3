package com.tweetapp.exception;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tweetapp.model.OutputDto;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandling extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<OutputDto<String>> handleExceptions( MethodArgumentTypeMismatchException exception, Principal principal) {
        OutputDto<String> response = new OutputDto<>();
        String message;
        message = "Invalid request body for parameter " + exception.getParameter().getParameterName();
        log.error("Invalid request body for parameter " + exception.getParameter().getParameterName()+" by user "+principal.getName(),exception);
        response.setError(true);
        response.setErrorMessage(message);
        return  new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}