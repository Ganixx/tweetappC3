package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.tweetapp.entity.AppUser;
import com.tweetapp.entity.Tweet;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.model.TweetResponseDto;
import com.tweetapp.repo.AppUserRepo;
import com.tweetapp.repo.TweetRepo;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = TweetServiceTests.class)
class TweetServiceTests {

    @Mock
    private TweetRepo tweetRepo;

    @Mock
    private AppUserRepo appUserRepo;

    @Test
    void testGetAllTweets() {
        Tweet tweet = new Tweet();
        tweet.setId("1");
        tweet.setTweetDescription("Hello World");
        tweet.setLoginId("1");

        Tweet tweet1 = new Tweet();
        tweet1.setId("2");
        tweet1.setTweetDescription("Hello World");
        tweet1.setLoginId("1");

        Mockito.when(tweetRepo.findAll()).thenReturn(Arrays.asList(tweet, tweet1));

        List<Tweet> tweets = tweetRepo.findAll();

        assertEquals(2, tweets.size());

    }

    @Test
    void testUpdateTweet() throws Exception {

        TweetServiceImpl tweetService = new TweetServiceImpl(tweetRepo, appUserRepo);
        Tweet tweet = new Tweet();
        tweet.setId("1");
        tweet.setTweetDescription("Hello World");
        tweet.setLoginId("ashok");

        Mockito.when(tweetRepo.findById("1")).thenReturn(Optional.of(tweet));

        TweetDto dto = new TweetDto();
        dto.setTweetDescription("Hello world modified");

        OutputDto<TweetDto> out = tweetService.updateTweet("1", dto, "ashok", "ashok");

        System.out.println(out);

        assertEquals("Hello world modified", out.getData().getTweetDescription());

    }

    @Test
    void testDeleteTweet() throws Exception {

        TweetServiceImpl tweetService = new TweetServiceImpl(tweetRepo, appUserRepo);
        Tweet tweet = new Tweet();
        tweet.setId("1");
        tweet.setTweetDescription("Hello World");
        tweet.setLoginId("ashok");

        Mockito.when(tweetRepo.findById("1")).thenReturn(Optional.of(tweet));

        OutputDto<Boolean> out = tweetService.deleteTweet("1", "ashok", "ashok");

        assertEquals(true, out.getData());

    }

    @Test
    void testDeleteTweetFail() throws Exception {

        TweetServiceImpl tweetService = new TweetServiceImpl(tweetRepo, appUserRepo);

        Exception exception = assertThrows(TweetNotFoundException.class, () -> {
            tweetService.deleteTweet("2", "ashok", "ashok");
        });

        String expectedMessage = "Tweet not found for id: 2";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void testGetTweetOfUser() throws Exception {
        TweetServiceImpl tweetService = new TweetServiceImpl(tweetRepo, appUserRepo);

        AppUser user = new AppUser();
        user.setLoginId("ashok");

        TweetResponseDto tweet = new TweetResponseDto();
        tweet.setId("1");
        tweet.setTweetDescription("Hello World");
        tweet.setLoginId("ashok");
        tweet.setLikes(new HashSet<>());
        tweet.setComments(new HashSet<>());
        tweet.setRetweets(new HashSet<>());

        TweetResponseDto tweet1 = new TweetResponseDto();
        tweet1.setId("2");
        tweet1.setTweetDescription("Hello World");
        tweet1.setLoginId("ramesh");
        tweet1.setLikes(new HashSet<>());
        tweet1.setComments(new HashSet<>());
        tweet1.setRetweets(new HashSet<>());

        PageImpl<TweetResponseDto> page = new PageImpl<>(Arrays.asList(tweet));

        Mockito.when(appUserRepo.customExistsByLoginId("ashok")).thenReturn(Optional.of(user));

        Mockito.when(tweetRepo.allTweetsOfuser("ashok", PageRequest.of(0, 5))).thenReturn(page);

        OutputDto<Page<TweetResponseDto>> tweets = tweetService.getTweetOfUser("ashok", "ashok", 0, 5);

        assertEquals(1, tweets.getData().getContent().size());
    }

    @Test
    void testPostTweet() {
        TweetServiceImpl tweetService = new TweetServiceImpl(tweetRepo, appUserRepo);
        TweetDto tweet = new TweetDto();
        tweet.setTweetDescription("Hello World");

        Tweet savedTweet = new Tweet();
        savedTweet.setId("1");
        savedTweet.setTweetDescription("Hello World");
        savedTweet.setLoginId("ashok");

        Mockito.when(tweetRepo.save(any(Tweet.class))).thenReturn(savedTweet);
        OutputDto<TweetDto> out = tweetService.addTweet(tweet, "ashok", "ashok");
        assertEquals(HttpStatus.CREATED, out.getHttpStatus());
    }

}
