package com.tweetapp.service;

public interface OtpService {
    public Boolean sendOtp(String email, String subject);

    public Boolean verifyOtp(String email, int otp);
}
