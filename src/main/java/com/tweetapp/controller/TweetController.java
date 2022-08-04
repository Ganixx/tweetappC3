package com.tweetapp.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.TweetDto;
import com.tweetapp.model.TweetResponseDto;
import com.tweetapp.service.TweetServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/tweets")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class TweetController {
    private final TweetServiceImpl tweetService;

    @PostMapping("{username}/add")
    public ResponseEntity<OutputDto<TweetDto>> addTweet(@PathVariable String username,
            @Valid @RequestBody TweetDto tweetDto,
            Principal principal) {
        OutputDto<TweetDto> result = tweetService.addTweet(tweetDto, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("{username}/like/{id}")
    public ResponseEntity<OutputDto<Boolean>> likeTweet(@PathVariable String username, @PathVariable String id,
            Principal principal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> result = tweetService.likeTweet(id, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("{username}/unlike/{id}")
    public ResponseEntity<OutputDto<Boolean>> unlikeTweet(@PathVariable String username, @PathVariable String id,
            Principal principal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> result = tweetService.unlikeTweet(id, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @DeleteMapping("{username}/delete/{id}")
    public ResponseEntity<OutputDto<Boolean>> deleteTweet(@PathVariable String username, @PathVariable String id,
            Principal principal) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Boolean> result = tweetService.deleteTweet(id, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("{username}/update/{id}")
    public ResponseEntity<OutputDto<TweetDto>> updateTweet(@PathVariable String username,
            @Valid @RequestBody TweetDto tweetDto,
            Principal principal, @PathVariable String id) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<TweetDto> result = tweetService.updateTweet(id, tweetDto, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/{username}")
    public ResponseEntity<OutputDto<Page<TweetResponseDto>>> getAlltweetsOfUser(@PathVariable String username,
            Principal principal, @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Page<TweetResponseDto>> result = tweetService.getTweetOfUser(principal.getName(), username, page,
                size);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/home")
    public ResponseEntity<OutputDto<Page<TweetResponseDto>>> homePage(Principal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size)
            throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<Page<TweetResponseDto>> result = tweetService.getTweetsForUserHomePage(principal.getName(), page,
                size);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
