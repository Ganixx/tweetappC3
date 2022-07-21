package com.tweetapp.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.tweetapp.entity.OTP;
import com.tweetapp.model.EmailDetails;
import com.tweetapp.repo.AppUserRepo;
import com.tweetapp.repo.OtpRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final EmailService emailService;
    private final OtpRepo otpRepo;
    private final AppUserRepo appUserRepo;

    @Override
    public Boolean sendOtp(String email, String subject) {
        if (subject.equals("register")) {
            if (appUserRepo.customExistsByEmail(email).isPresent()) {
                return false;
            }
        } else if (subject.equals("forgot") && !appUserRepo.customExistsByEmail(email).isPresent()) {
            return false;
        }
        switch (subject) {
            case "forgot":
                subject = "Your OTP for reset password with us is:";
                break;
            case "register":
                subject = "Your OTP for registering with us is:";
                break;
            default:
                return false;
        }
        if (email.length() == 0) {
            return false;
        }
        otpRepo.findByEmail(email).ifPresent(otpRepo::delete);
        Double random;
        do {
            random = Math.random() * 999999;

        } while (random < 99999);
        int intValue = random.intValue();
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setOtpValue(intValue);
        otp.setCreatedDate(Date.from(Instant.now()));
        otpRepo.save(otp);
        EmailDetails details = new EmailDetails();
        details.setRecipient(email);
        details.setMsgBody(subject + " " + intValue);
        details.setSubject("Verification passcode from Tweet app");
        return emailService.sendSimpleMail(details);
    }

    @Override
    public Boolean verifyOtp(String email, int otpValue) {
        if (email.length() == 0) {
            return false;
        }
        var ans = otpRepo.findByEmail(email)
                .map(OTP::getOtpValue)
                .orElse(0);
       
        return ans == otpValue ;
    }

}
