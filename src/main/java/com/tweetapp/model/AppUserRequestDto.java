package com.tweetapp.model;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppUserRequestDto {
    private String id;
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "password is mandatory")
    private String password;
    @NotBlank(message = "loginId is mandatory")
    private String loginId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String image;
    @Range(min = 100000, max = 999999, message = "valid otp required")
    private int otp;
}
