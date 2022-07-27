package com.tweetapp.service;

import org.springframework.data.domain.Page;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.model.TweetResponseDto;

public interface TweetService {
    OutputDto<TweetDto> addTweet(TweetDto tweetDto, String userIdFromRequest ,String userIdFromPrincipal) throws AppUserNotFoundException;
    OutputDto<Boolean> likeTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal) throws AppUserNotFoundException , TweetNotFoundException;
    OutputDto<Boolean> unlikeTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal) throws AppUserNotFoundException , TweetNotFoundException;
    OutputDto<Boolean> deleteTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal) throws AppUserNotFoundException , TweetNotFoundException;
    OutputDto<TweetDto> updateTweet (String tweetId,TweetDto tweetDto, String userIdFromRequest, String userIdFromPrincipal) throws AppUserNotFoundException, TweetNotFoundException;
    OutputDto<Page<TweetResponseDto>> getTweetOfUser(String principal,String userIdFromRequest,int page, int size) throws AppUserNotFoundException, TweetNotFoundException;
    OutputDto<Page<TweetResponseDto>> getTweetsForUserHomePage(String principal,int page, int size) throws AppUserNotFoundException, TweetNotFoundException;
}
