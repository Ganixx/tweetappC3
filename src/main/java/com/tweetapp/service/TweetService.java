package com.tweetapp.service;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;

public interface TweetService {
    OutputDto<TweetDto> addTweet(TweetDto tweetDto, String userIdFromRequest ,String userIdFromPrincipal) throws AppUserNotFoundException;
    OutputDto<Boolean> likeTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal) throws AppUserNotFoundException , TweetNotFoundException;
}
