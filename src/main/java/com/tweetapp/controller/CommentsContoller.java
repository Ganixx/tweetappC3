package com.tweetapp.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.exception.AppUserNotFoundException;
import com.tweetapp.exception.CommentNotFoundException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.CommentReqDto;
import com.tweetapp.model.CommentResDto;
import com.tweetapp.model.OutputDto;
import com.tweetapp.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/tweets")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class CommentsContoller {
    private final CommentService commentService;

    @PostMapping("{username}/reply/{id}")
    public ResponseEntity<OutputDto<CommentResDto>> addComment(@PathVariable String username,
            @Valid @RequestBody CommentReqDto commentReqDto,
            Principal principal, @PathVariable String id) throws AppUserNotFoundException, TweetNotFoundException {
        OutputDto<CommentResDto> result = commentService.addComment(commentReqDto, id, username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("comment/like/{id}")
    public ResponseEntity<OutputDto<Boolean>> likeComment(@PathVariable String id, Principal principal)
            throws CommentNotFoundException {
        OutputDto<Boolean> result = commentService.likeComment(id, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PutMapping("comment/dislike/{id}")
    public ResponseEntity<OutputDto<Boolean>> dislikeComment(@PathVariable String id, Principal principal)
            throws CommentNotFoundException {
        OutputDto<Boolean> result = commentService.dislikeComment(id, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("comment/findall/{id}")
    public ResponseEntity<OutputDto<Page<CommentResDto>>> findAll(@PathVariable String id, Principal principal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws TweetNotFoundException {
        OutputDto<Page<CommentResDto>> result = commentService.findAll(id, page, size, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

}
