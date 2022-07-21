package com.tweetapp.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.entity.OTP;

@Repository
public interface OtpRepo extends MongoRepository<OTP, String>{
    public Optional<OTP> findByEmail(String email);
}
