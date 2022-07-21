package com.tweetapp.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "otp")

public class OTP {
    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private int otpValue;
    @Indexed(name = "created_date_index", expireAfterSeconds = 625)
    private Date createdDate;
}
