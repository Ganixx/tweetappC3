package com.tweetapp.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchAppUserResponseDto {
    private String id;
    private String email;
    private String loginId;
    private String firstName;
    private String lastName;
    private String image;
    private boolean youFollow;
    private Set<String> followers;
}
