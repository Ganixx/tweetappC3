package com.tweetapp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed(name = "tweet_created_date_index")
    private Date createdDate;
    private Date modifiedDate;
    private Set<String> likes = new HashSet<>();
}
