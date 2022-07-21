package com.tweetapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.entity.AppUser;
import com.tweetapp.exception.InvalidOtpException;
import com.tweetapp.exception.UserAlreadyExsists;
import com.tweetapp.model.AppUserRequestDto;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.FollowDto;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.Roles;
import com.tweetapp.repo.AppUserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public List<AppUser> getAllAppUsers() {
        return appUserRepo.findAll();
    }

    @Override
    public AppUserResponseDto createUser(AppUserRequestDto userReq) throws InvalidOtpException, UserAlreadyExsists {
        AppUser user = new AppUser();
        user.setEmail(userReq.getEmail());
        user.setLoginId(userReq.getLoginId());
        user.setPassword(userReq.getPassword());
        user.setFirstName(userReq.getFirstName());
        user.setLastName(userReq.getLastName());
        user.setContactNumber(userReq.getContactNumber());
        user.setImage(userReq.getImage());
        user.setOtp(userReq.getOtp());
        if (Boolean.FALSE.equals(otpService.verifyOtp(user.getEmail(), user.getOtp()))) {
            throw new InvalidOtpException("Invalid OTP");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRole().add(Roles.ROLE_USER);
        AppUser createdUser;
        AppUserResponseDto responseDto = new AppUserResponseDto();
        try {
            createdUser = appUserRepo.save(user);
            responseDto.setEmail(createdUser.getEmail());
            responseDto.setLoginId(createdUser.getLoginId());
            responseDto.setId(createdUser.getId());
            responseDto.setFirstName(createdUser.getFirstName());
            responseDto.setLastName(createdUser.getLastName());
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExsists("User already exists with this email id or login id ");
        }
        return responseDto;
    }

    @Override
    public Boolean existsByLoginId(String loginId) throws UserAlreadyExsists {
        if (appUserRepo.customExistsByLoginId(loginId).isPresent()) {
            throw new UserAlreadyExsists("User already exists");
        }
        return false;
    }

    @Override
    public Boolean existsByEmail(String email) throws UserAlreadyExsists {
        if (appUserRepo.customExistsByEmail(email).isPresent()) {
            throw new UserAlreadyExsists("User already exists");
        }
        return false;
    }


    @Override
    public String followHelper(FollowDto followDto,String userId) throws UsernameNotFoundException {
        AppUser user = appUserRepo.customExistsByLoginId(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found " + userId));
        AppUser follow = appUserRepo.customExistsByLoginId(followDto.getFollowId()).orElseThrow(
                () -> new UsernameNotFoundException("followId not found " + followDto.getFollowId()));
        if(followDto.getFollowType().equals(FollowDto.FollowType.FOLLOW)) {
            if( !user.getFollowing().add(followDto.getFollowId())) {
                return "Already following the User";
            }
            follow.getFollowers().add(userId);
            appUserRepo.save(user);
            appUserRepo.save(follow);
            return "Successfully followed the User";
        } else if(followDto.getFollowType().equals(FollowDto.FollowType.UNFOLLOW)){
            if( !user.getFollowing().remove(followDto.getFollowId())) {
                return "Already unfollowed the User";
            }
            follow.getFollowers().remove(userId);
            appUserRepo.save(user);
            appUserRepo.save(follow);
            return "Successfully unfollowed the User";
        }
        return "Bad request";
    }

    @Override
    public OutputDto<List<AppUserResponseDto>> searchUser(String searchText) {
        OutputDto<List<AppUserResponseDto>> outputDto = new OutputDto<>();
        try{
            outputDto.setData(appUserRepo.searchByLoginId(searchText).orElse(new ArrayList<>()));
            outputDto.setError(false);
            outputDto.setErrorMessage("");
            outputDto.setHttpStatus(HttpStatus.OK);
        } catch(Exception e) {
            log.error(e.getMessage());
            outputDto.setError(true);
            outputDto.setErrorMessage("server error");
            outputDto.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return outputDto;
    }

    
   

    @Override
    public Boolean updatePassword(ForgotPassword forgotPassword) throws InvalidOtpException, UsernameNotFoundException {
        AppUser appuser = appUserRepo.customExistsByEmail(
                forgotPassword.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (Boolean.FALSE.equals(otpService.verifyOtp(forgotPassword.getEmail(), forgotPassword.getOtp()))) {
            throw new InvalidOtpException("Invalid OTP");
        }
        appuser.setPassword(passwordEncoder.encode(forgotPassword.getPassword()));
        appUserRepo.save(appuser);
        return true;
    }

}
