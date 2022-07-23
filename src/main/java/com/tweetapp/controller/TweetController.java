package com.tweetapp.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.service.TweetServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/tweets")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class TweetController {
    private final TweetServiceImpl tweetService;

    @PostMapping("{username}/add")
    public ResponseEntity<OutputDto<TweetDto>> searchUser(@PathVariable String username,@Valid @RequestBody TweetDto tweetDto,
    Principal principal) {
        OutputDto<TweetDto> result  = tweetService.addTweet(tweetDto,username,principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("{username}/like/{id}")
    public ResponseEntity<OutputDto<Boolean>> likeTweet(@PathVariable String username,@PathVariable String id,
    Principal principal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> result  = tweetService.likeTweet(id, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
