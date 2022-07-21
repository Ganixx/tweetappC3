package com.tweetapp.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.tweetapp.model.EmailDetails;
 
@Service
public class EmailServiceImpl implements EmailService {
 
    @Autowired private JavaMailSender javaMailSender;
 
    @Value("${spring.mail.email}") private String sender;
 
    public Boolean sendSimpleMail(EmailDetails details)
    {
 
        try {
 
            SimpleMailMessage mailMessage
                = new SimpleMailMessage();
 
       
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
 
         
            javaMailSender.send(mailMessage);
            return true;
        }

        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
 

}
