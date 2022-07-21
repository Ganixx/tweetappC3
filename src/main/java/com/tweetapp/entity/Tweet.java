package com.tweetapp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "tweets")
public class Tweet {
    @Id
    private String id;
    private Double version = 1.0;
    @NotBlank(message = "tweetDescription is mandatory")
    @Size(min = 1, max = 150, message = "tweetDescription must be between 1 and 150 characters")
    private String tweetDescription;
    private String tweetImage;
    @NotBlank(message = "loginId is mandatory")
    private String loginId;
    @Indexed(name = "tweet_created_date_index")
    private Date createdDate;
    private Date modifiedDate;
    private Set<String> likes = new HashSet<>();
    private Set<String> comments = new HashSet<>();
    private Set<String> retweets = new HashSet<>();
    @TextIndexed
    private Set<String> hashtags = new HashSet<>();
    
}
