package com.tweetapp.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResDto {
    private String id;
    private String loginId;
    private String reply;
    private Date createdDate;
    private Boolean isLiked;
    private int likeCount;
    private Set<String> likes = new HashSet<>();
    private boolean isOwner;
}
