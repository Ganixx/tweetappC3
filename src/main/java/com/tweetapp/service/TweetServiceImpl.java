package com.tweetapp.service;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tweetapp.common.TweetConstants;
import com.tweetapp.entity.AppUser;
import com.tweetapp.entity.Tweet;
import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.model.TweetResponseDto;
import com.tweetapp.repo.AppUserRepo;
import com.tweetapp.repo.TweetRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetRepo tweetRepo;
    private final AppUserRepo appUserRepo;

    @Override
    public OutputDto<TweetDto> addTweet(TweetDto tweetDto, String userIdFromRequest, String userIdFromPrincipal) {
        OutputDto<TweetDto> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    UserId from principal and tweetDto loginId are not same.
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
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    For like tweet, UserId from principal and tweetDto loginId are not same.
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

    }

    @Override
    public OutputDto<Boolean> unlikeTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    For unlike tweet, UserId from principal and tweetDto loginId are not same.
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
                    .orElseThrow(() -> new TweetNotFoundException("Tweet not found for id:: " + tweetId));
            if (!tweet.getLikes().contains(userIdFromPrincipal)) {
                log.error("UserId: {} already unliked tweetId: {}", userIdFromPrincipal, tweetId);
                output.setError(true);
                output.setErrorMessage("user already unliked tweet");
                output.setData(false);
                output.setHttpStatus(HttpStatus.CONFLICT);
                return output;
            }
            tweet.getLikes().remove(userIdFromPrincipal);
            tweetRepo.save(tweet);
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(true);
            output.setHttpStatus(HttpStatus.OK);
            return output;
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else if (e.getClass() == TweetNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while unliking tweet: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while unliking tweet");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
    }

    @Override
    public OutputDto<Boolean> deleteTweet(String tweetId, String userIdFromRequest, String userIdFromPrincipal)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    For delete tweet, UserId from principal and tweetDto loginId are not same.
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
                    .orElseThrow(() -> new TweetNotFoundException("Tweet not found for id: " + tweetId));
            if (!tweet.getLoginId().equals(userIdFromPrincipal)) {
                log.error("UserId: {} is not authorized to delete tweetId: {}", userIdFromPrincipal, tweetId);
                output.setError(true);
                output.setErrorMessage("user is not authorized to delete tweet");
                output.setData(false);
                output.setHttpStatus(HttpStatus.UNAUTHORIZED);
                return output;
            }
            tweetRepo.delete(tweet);
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(true);
            output.setHttpStatus(HttpStatus.OK);
            return output;
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else if (e.getClass() == TweetNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while deleting tweet: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while deleting tweet");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
    }

    @Override
    public OutputDto<Page<TweetResponseDto>> getTweetOfUser(String principal, String userIdFromRequest, int page,
            int size)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Page<TweetResponseDto>> output = new OutputDto<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            AppUser user = appUserRepo.customExistsByLoginId(userIdFromRequest).orElseThrow(
                    () -> new UsernameNotFoundException("User not found " + userIdFromRequest));
            Page<TweetResponseDto> tweetPage = tweetRepo.allTweetsOfuser(user.getLoginId(), pageable);
            tweetPage.getContent().stream().forEach(tweet -> {
                tweet.setIsLiked(tweet.getLikes().contains(principal));
                tweet.setLikeCount(tweet.getLikes().size());
                tweet.setLikes(null);
                tweet.setIsRetweeted(tweet.getRetweets().contains(principal));
                tweet.setRetweetCount(tweet.getRetweets().size());
                tweet.setRetweets(null);
                tweet.setIsOwner(tweet.getLoginId().equals(principal));
                tweet.setReplyCount(tweet.getComments().size());
                tweet.setComments(null);
            });
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(tweetPage);
            output.setHttpStatus(HttpStatus.OK);
            output.setTypeOfPage(true);
            return output;
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while getting tweet of user: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while getting tweet of user");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
    }

    @Override
    public OutputDto<TweetDto> updateTweet(String tweetId, TweetDto tweetDto, String userIdFromRequest,
            String userIdFromPrincipal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<TweetDto> output = new OutputDto<>();
        if (!userIdFromRequest.equals(userIdFromPrincipal)) {
            log.error("""
                    For update tweet, UserId from principal and tweetDto loginId are not same.
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
                    .orElseThrow(() -> new TweetNotFoundException("Tweet not found for id: " + tweetId));
            if (!tweet.getLoginId().equals(userIdFromPrincipal)) {
                log.error("UserId: {} is not authorized to update tweetId: {}", userIdFromPrincipal, tweetId);
                output.setError(true);
                output.setErrorMessage("user is not authorized to update tweet");
                output.setData(null);
                output.setHttpStatus(HttpStatus.UNAUTHORIZED);
                return output;
            }
            tweet.setTweetDescription(tweetDto.getTweetDescription());
            tweet.setModifiedDate(new Date());
            tweetRepo.save(tweet);
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(tweetDto);
            output.setHttpStatus(HttpStatus.ACCEPTED);
            return output;
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else if (e.getClass() == TweetNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while updating tweet: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while updating tweet");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
    }

    @Override
    public OutputDto<Page<TweetResponseDto>> getTweetsForUserHomePage(String principal, int page, int size)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Page<TweetResponseDto>> output = new OutputDto<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            AppUser user = appUserRepo.customExistsByLoginId(principal).orElseThrow(
                    () -> new UsernameNotFoundException("User not found " + principal));
            Page<TweetResponseDto> tweetPage = tweetRepo.tweetsForUserHomePage(user.getFollowing(), pageable);
            tweetPage.getContent().stream().forEach(tweet -> {
                tweet.setIsLiked(tweet.getLikes().contains(principal));
                tweet.setLikeCount(tweet.getLikes().size());
                tweet.setLikes(null);
                tweet.setIsRetweeted(tweet.getRetweets().contains(principal));
                tweet.setRetweetCount(tweet.getRetweets().size());
                tweet.setRetweets(null);
                tweet.setIsOwner(tweet.getLoginId().equals(principal));
                tweet.setReplyCount(tweet.getComments().size());
                tweet.setComments(null);
            });
            output.setError(false);
            output.setErrorMessage(null);
            output.setData(tweetPage);
            output.setHttpStatus(HttpStatus.OK);
            output.setTypeOfPage(true);
            return output;
        } catch (Exception e) {
            if (e.getClass() == AppUserNotFoundException.class) {
                throw e;
            } else {
                log.error("Exception while getting tweets for user: {}", e.getMessage());
                output.setError(true);
                output.setErrorMessage("internal error while getting tweets for user");
                output.setData(null);
                output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                return output;
            }
        }
    }
}
