package com.tweetapp.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TweetDto {
    private String id;
    @NotBlank(message = "tweetDescription is mandatory")
    @Size(min = 1, max = 150, message = "tweetDescription must be between 1 and 144 characters")
    private String tweetDescription;
    private String tweetImage;
}
