package com.tweetapp.service;
import com.tweetapp.model.EmailDetails;
 

public interface EmailService {
 
    Boolean sendSimpleMail(EmailDetails details);
 
}