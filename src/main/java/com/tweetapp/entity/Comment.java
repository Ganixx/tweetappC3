package com.tweetapp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String loginId;
    private Double version = 1.0;
    @Size(min = 1, max = 150, message = "tweetDescription must be between 1 and 144 characters")
    private String reply;
    @Indexed(name = "tweet_created_date_index")
    private Date createdDate;
    private Set<String> likes = new HashSet<>();
    @TextIndexed
    private Set<String> hashtags = new HashSet<>();
}
