package com.tweetapp.model;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentReqDto {
    @Size(min = 1, max = 150, message = "tweetDescription must be between 1 and 144 characters")
    private String reply;
}
