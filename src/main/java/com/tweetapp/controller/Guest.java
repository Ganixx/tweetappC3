package com.tweetapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.InvalidOtpException;
import com.tweetapp.exception.UserAlreadyExsists;
import com.tweetapp.model.AppUserRequestDto;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.OutputDto;
import com.tweetapp.service.AppUserService;
import com.tweetapp.service.OtpService;
import com.tweetapp.service.ProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/tweets/guest")
@RequiredArgsConstructor
@Slf4j
public class Guest {
    private final AppUserService appUserService;
    private final OtpService otpService;
    private final ProducerService producerService;

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam String message) {
        producerService.sendMessage(message);
    }

    @PostMapping("/register")
    public ResponseEntity<OutputDto<AppUserResponseDto>> signUp(@Valid @RequestBody AppUserRequestDto user) {
        OutputDto<AppUserResponseDto> outputDto = new OutputDto<>();
        try {
            outputDto.setData(appUserService.createUser(user));
            outputDto.setErrorMessage("");
            outputDto.setError(false);
            return ResponseEntity.status(HttpStatus.CREATED).body(outputDto);
        } catch (InvalidOtpException e) {
            outputDto.setErrorMessage(e.getMessage());
            outputDto.setError(true);
            outputDto.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(outputDto);
        } catch (UserAlreadyExsists e) {
            outputDto.setErrorMessage(e.getMessage());
            outputDto.setError(true);
            outputDto.setData(null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(outputDto);
        }
    }

    @PostMapping("/forgot")
    public ResponseEntity<OutputDto<Boolean>> forgotPassword(@Valid @RequestBody ForgotPassword forgotPassword) {
        OutputDto<Boolean> outputDto = new OutputDto<>();
        try {
            outputDto.setData(appUserService.updatePassword(forgotPassword));
            outputDto.setErrorMessage("");
            outputDto.setError(false);
            return ResponseEntity.ok().body(outputDto);
        } catch (InvalidOtpException e) {
            outputDto.setErrorMessage(e.getMessage());
            outputDto.setError(true);
            outputDto.setData(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(outputDto);
        } catch (UsernameNotFoundException e) {
            outputDto.setErrorMessage(e.getMessage());
            outputDto.setError(true);
            outputDto.setData(false);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(outputDto);
        }
    }

    @GetMapping("/generateOtp/{email}/{subject}")
    public ResponseEntity<Boolean> generateOtp(@PathVariable String email, @PathVariable String subject) {
        try {
            if (Boolean.TRUE.equals(otpService.sendOtp(email, subject))) {
                return ResponseEntity.status(HttpStatus.CREATED).body(true);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/emailExists/{email}")
    public ResponseEntity<Boolean> emailExists(@PathVariable String email) {
        try {
            appUserService.existsByEmail(email);
            return ResponseEntity.ok().body(false);
        } catch (UserAlreadyExsists e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(true);
        }
    }

    @GetMapping("/loginIdExists/{loginId}")
    public ResponseEntity<Boolean> loginIdExists(@PathVariable String loginId) {
        try {
            appUserService.existsByLoginId(loginId);
            return ResponseEntity.ok().body(false);
        } catch (UserAlreadyExsists e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(true);
        }
    }

    @GetMapping("/verifyOtp")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam(name = "email") String email,
            @RequestParam(name = "otp") int otp) {
        return ResponseEntity.ok().body(otpService.verifyOtp(email, otp));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
