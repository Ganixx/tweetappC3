package com.tweetapp.model;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TweetResponseDto {
    private String id;
    private String tweetId;
    private String tweetDescription;
    private String loginId;
    private int likeCount;
    private int replyCount;
    private Date createdDate;
    private int retweetCount;
    private Boolean isOwner;
    private Boolean isLiked;
    private Boolean isRetweeted;
    private Set<String> likes;
    private Set<String> comments;
    private Set<String> retweets;
    private Set<String> hashtags;
    private Set<String> mentions;
}
