package com.tweetapp.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tweetapp.model.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "users")
public class AppUser {
    @Id
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
    private List<Roles> role = new ArrayList<>();
    @Transient
    @Range(min = 100000, max = 999999, message = "valid otp required")
    private int otp;
    private Set<String> followers =  new HashSet<>();
    private Set<String> following = new HashSet<>();
}
