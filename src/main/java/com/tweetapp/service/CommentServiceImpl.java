package com.tweetapp.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tweetapp.common.TweetConstants;
import com.tweetapp.entity.Comment;
import com.tweetapp.entity.Tweet;
import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.CommentNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.CommentReqDto;
import com.tweetapp.model.CommentResDto;
import com.tweetapp.model.OutputDto;
import com.tweetapp.repo.CommentRepo;
import com.tweetapp.repo.TweetRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepo commentRepo;
    private final TweetRepo tweetRepo;

    @Override
    public OutputDto<CommentResDto> addComment(CommentReqDto comment, String tweetId, String userIdFromRequest,
            String userIdFromPrincipal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<CommentResDto> output = new OutputDto<>();
        CommentResDto commentResDto = new CommentResDto();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    UserId from principal and commentDto loginId are not same.
                    UserId from principal: {}
                    TweetDto loginId: {}
                        """, userIdFromPrincipal, userIdFromRequest);
            output.setErrorMessage(TweetConstants.BADUSERID);
            output.setError(true);
            output.setData(null);
            output.setHttpStatus(HttpStatus.BAD_REQUEST);
            return output;
        }
        try {
            Tweet tweet = tweetRepo.findById(tweetId)
                    .orElseThrow(() -> new TweetNotFoundException("Tweet not found for id " + tweetId));
            Comment commentEntity = new Comment();
            commentEntity.setReply(comment.getReply());
            commentEntity.setCreatedDate(new Date());
            commentEntity.setLoginId(userIdFromPrincipal);
            commentEntity.setTweetId(tweet.getId());
            Comment postSave = commentRepo.save(commentEntity);
            commentResDto.setId(postSave.getId());
            commentResDto.setReply(postSave.getReply());
            commentResDto.setCreatedDate(postSave.getCreatedDate());
            commentResDto.setLoginId(postSave.getLoginId());
            tweet.getComments().add(postSave.getId());
            tweetRepo.save(tweet);
            output.setError(false);
            output.setData(commentResDto);
            output.setHttpStatus(HttpStatus.OK);
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else if (e.getClass() == TweetNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while liking tweet: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while liking tweet");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
        return output;
    }

    @Override
    public OutputDto<Page<CommentResDto>> findAll(String tweetId, int page, int size, String userIdFromPrincipal)
            throws TweetNotFoundException {
        OutputDto<Page<CommentResDto>> output = new OutputDto<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Tweet tweet = tweetRepo.findById(tweetId)
                    .orElseThrow(() -> new TweetNotFoundException("Tweet not found for id " + tweetId));
            Page<CommentResDto> commentResDtoPage = commentRepo.customFindAll(tweet.getId(), pageable);
            commentResDtoPage.getContent().forEach(commentResDto -> {
                commentResDto.setLikeCount(commentResDto.getLikes().size());
                commentResDto.setIsLiked(commentResDto.getLikes().contains(userIdFromPrincipal));
                commentResDto.setLikes(null);
                commentResDto.setOwner(commentResDto.getLoginId().equals(userIdFromPrincipal));
            });
            output.setError(false);
            output.setData(commentResDtoPage);
            output.setHttpStatus(HttpStatus.OK);
        } catch (Exception e) {
            if (e.getClass() == TweetNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while finding comments: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while finding comments");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }

        }
        return output;

    }

    @Override
    public OutputDto<Boolean> likeComment(String commentId, String userIdFromPrincipal)
            throws CommentNotFoundException {
        OutputDto<Boolean> output = new OutputDto<>();
        try {
            Comment comment = commentRepo.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found for id " + commentId));
            if (comment.getLikes().contains(userIdFromPrincipal)) {
                output.setError(true);
                output.setErrorMessage("You have already liked this comment");
                output.setData(false);
                output.setHttpStatus(HttpStatus.BAD_REQUEST);
                return output;
            }
            comment.getLikes().add(userIdFromPrincipal);
            commentRepo.save(comment);
            output.setError(false);
            output.setData(true);
            output.setHttpStatus(HttpStatus.OK);
        } catch (Exception e) {
            if (e.getClass() == CommentNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while liking comment: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while liking comment");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
        return output;
    }

    @Override
    public OutputDto<Boolean> dislikeComment(String commentId, String userIdFromPrincipal)
            throws CommentNotFoundException {
        OutputDto<Boolean> output = new OutputDto<>();
        try {
            Comment comment = commentRepo.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found for id " + commentId));
            if (!comment.getLikes().contains(userIdFromPrincipal)) {
                output.setError(true);
                output.setErrorMessage("You have not liked this comment");
                output.setData(false);
                output.setHttpStatus(HttpStatus.BAD_REQUEST);
                return output;
            }
            comment.getLikes().remove(userIdFromPrincipal);
            commentRepo.save(comment);
            output.setError(false);
            output.setData(true);
            output.setHttpStatus(HttpStatus.OK);
        } catch (Exception e) {
            if (e.getClass() == CommentNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while disliking comment: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while disliking comment");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
        return output;
    }

}
