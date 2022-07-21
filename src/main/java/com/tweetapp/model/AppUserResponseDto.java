package com.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppUserResponseDto {
    private String id;
    private String email;
    private String loginId;
    private String firstName;
    private String lastName;
    private String image;
}
