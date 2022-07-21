package com.tweetapp.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {
    public enum FollowType {
        FOLLOW, UNFOLLOW
    }
    @NotNull(message = "followType is mandatory")
    private FollowType followType;
    @NotBlank(message = "followId is mandatory")
    private String followId;
}
