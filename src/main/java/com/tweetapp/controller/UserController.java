package com.tweetapp.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.FollowDto;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.SearchAppUserResponseDto;
import com.tweetapp.service.AppUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class UserController {
    private final AppUserService appUserService;

    @GetMapping("/users/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OutputDto<Page<AppUserResponseDto>>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        OutputDto<Page<AppUserResponseDto>> result = appUserService.getAllAppUsers(page, size);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/follow")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> follow(@Valid @RequestBody FollowDto followDto, Principal principal) {
        return ResponseEntity.ok().body(appUserService.followHelper(followDto, principal.getName()));
    }

    @GetMapping("user/search/{username}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OutputDto<Page<SearchAppUserResponseDto>>> searchUser(@PathVariable String username,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,Principal principal) {
        OutputDto<Page<SearchAppUserResponseDto>> result = appUserService.searchUser(username,principal.getName(), page, size);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OutputDto<AppUserResponseDto>> findUserbyId(Principal principal) {
        OutputDto<AppUserResponseDto> result = appUserService.findUserbyId(principal.getName(), principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("userById/{username}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OutputDto<AppUserResponseDto>> getUserbyId(@PathVariable String username,
            Principal principal) {
        OutputDto<AppUserResponseDto> result = appUserService.findUserbyId(username, principal.getName());
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

}
