package com.tweetapp.service;

import org.springframework.data.domain.Page;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.CommentNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.CommentReqDto;
import com.tweetapp.model.CommentResDto;
import com.tweetapp.model.OutputDto;

public interface CommentService {

    OutputDto<CommentResDto> addComment(CommentReqDto comment, String tweetId, String userIdFromRequest , String userIdFromPrincipal)
    throws AppUserNotFoundException,TweetNotFoundException;

    OutputDto<Page<CommentResDto>> findAll(String tweetId, int page , int size , String userIdFromPrincipal) throws TweetNotFoundException;

    OutputDto<Boolean> likeComment(String commentId, String userIdFromPrincipal) throws CommentNotFoundException;

    OutputDto<Boolean> dislikeComment(String commentId, String userIdFromPrincipal) throws CommentNotFoundException;

    
}
