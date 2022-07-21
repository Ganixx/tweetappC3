package com.tweetapp.service;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.tweetapp.entity.AppUser;
import com.tweetapp.exception.InvalidOtpException;
import com.tweetapp.exception.UserAlreadyExsists;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.FollowDto;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.OutputDto;

public interface AppUserService {
    public List<AppUser> getAllAppUsers();

    public AppUserResponseDto createUser(AppUser user) throws InvalidOtpException, UserAlreadyExsists;

    public Boolean existsByLoginId(String loginId) throws UserAlreadyExsists;

    public Boolean existsByEmail(String email) throws UserAlreadyExsists;

    public Boolean updatePassword(ForgotPassword forgotPassword) throws InvalidOtpException, UsernameNotFoundException;

    public String followHelper(FollowDto followDto,String userId) throws UsernameNotFoundException;

    public OutputDto<List<AppUserResponseDto>> searchUser(String searchText);

}
