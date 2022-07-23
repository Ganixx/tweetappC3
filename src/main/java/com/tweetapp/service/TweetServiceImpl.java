package com.tweetapp.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tweetapp.entity.Tweet;
import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.repo.TweetRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetRepo tweetRepo;

    @Override
    public OutputDto<TweetDto> addTweet(TweetDto tweetDto, String userIdFromRequest ,String userIdFromPrincipal) {
        OutputDto<TweetDto> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    UserId from principal and tweetDto loginId are not same.
                    UserId from principal: {}
                    TweetDto loginId: {}
                        """, userIdFromPrincipal, userIdFromRequest);
            output.setErrorMessage("bad userId");
            output.setError(true);
            output.setData(null);
            output.setHttpStatus(HttpStatus.BAD_REQUEST);
            return output;
        }
        try {
            Tweet tweet = new Tweet();
            tweet.setLoginId(userIdFromPrincipal);
            tweet.setTweetDescription(tweetDto.getTweetDescription());
            tweet.setCreatedDate(Date.from(Instant.now()));
            tweet.setModifiedDate(Date.from(Instant.now()));
            tweet.setTweetImage(tweetDto.getTweetImage());
            Tweet postedTweet = tweetRepo.save(tweet);
            tweetDto.setId(postedTweet.getId());
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(tweetDto);
            output.setHttpStatus(HttpStatus.CREATED);
            return output;
        } catch (Exception e) {
            log.error("Exception while adding tweet: {}", e.getMessage());
            output.setError(true);
            output.setErrorMessage("internal error while adding tweet");
            output.setData(null);
            output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return output;
        }

    }

    @Override
    public OutputDto<Boolean> likeTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal)
            throws AppUserNotFoundException ,TweetNotFoundException{
        OutputDto<Boolean> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    For like tweet, UserId from principal and tweetDto loginId are not same.
                    UserId from principal: {}
                    TweetDto loginId: {}
                        """, userIdFromPrincipal, userIdFromRequest);
            output.setErrorMessage("bad userId");
            output.setError(true);
            output.setData(null);
            output.setHttpStatus(HttpStatus.BAD_REQUEST);
            return output;
        }
        try {
            Tweet tweet = tweetRepo.findById(tweetId).orElseThrow(() -> new TweetNotFoundException("Tweet not found for id: " + tweetId));
            if (tweet.getLikes().contains(userIdFromPrincipal)) {
                log.error("UserId: {} already liked tweetId: {}", userIdFromPrincipal, tweetId);
                output.setError(true);
                output.setErrorMessage("user already liked tweet");
                output.setData(false);
                output.setHttpStatus(HttpStatus.CONFLICT);
                return output;
            }
            tweet.getLikes().add(userIdFromPrincipal);
            tweetRepo.save(tweet);
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(true);
            output.setHttpStatus(HttpStatus.OK);
            return output;
        } catch (Exception e) {
            if(e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else if(e.getClass() == TweetNotFoundException.class) {
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

    }
}
