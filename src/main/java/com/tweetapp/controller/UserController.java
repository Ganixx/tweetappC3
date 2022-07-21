package com.tweetapp.controller;


import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.entity.AppUser;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.FollowDto;
import com.tweetapp.model.OutputDto;
import com.tweetapp.service.AppUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class UserController {
    private final AppUserService appUserService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(appUserService.getAllAppUsers());
    }

    @PostMapping("/follow")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> follow(@Valid @RequestBody FollowDto followDto,Principal principal) {
        return ResponseEntity.ok().body(appUserService.followHelper(followDto,principal.getName()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<OutputDto<List<AppUserResponseDto>>> searchUser(@RequestParam(name = "search") String searchText) {
        OutputDto<List<AppUserResponseDto>> result  = appUserService.searchUser(searchText);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
  
    
}
