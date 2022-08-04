package com.tweetapp.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.entity.AppUser;
import com.tweetapp.exception.InvalidOtpException;
import com.tweetapp.exception.UserAlreadyExsistsException;
import com.tweetapp.model.AppUserRequestDto;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.FollowDto;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.Roles;
import com.tweetapp.model.SearchAppUserResponseDto;
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

    public OutputDto<Page<AppUserResponseDto>> getAllAppUsers(int page, int size) {
        OutputDto<Page<AppUserResponseDto>> output = new OutputDto<>();
        try {

            Pageable paging = PageRequest.of(page, size);
            output.setHttpStatus(HttpStatus.OK);
            output.setData(appUserRepo.customFindAll(paging));
            output.setError(false);
            output.setErrorMessage("");
            output.setTypeOfPage(true);
            return output;
        } catch (IllegalArgumentException e) {
            log.error("Error in getAllAppUsers: {}", e.getMessage(), e);
            output.setHttpStatus(HttpStatus.BAD_REQUEST);
            output.setData(null);
            output.setError(true);
            output.setErrorMessage(e.getMessage());
            output.setTypeOfPage(false);
            return output;
        } catch (Exception e) {
            log.error("Error in getAllAppUsers: {}", e.getMessage(), e);
            output.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            output.setData(null);
            output.setError(true);
            output.setErrorMessage(e.getMessage());
            output.setTypeOfPage(false);
            return output;
        }

    }

    @Override
    public AppUserResponseDto createUser(AppUserRequestDto userReq)
            throws InvalidOtpException, UserAlreadyExsistsException {
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
            throw new UserAlreadyExsistsException("User already exists with this email id or login id ");
        }
        return responseDto;
    }

    @Override
    public Boolean existsByLoginId(String loginId) throws UserAlreadyExsistsException {
        if (appUserRepo.customExistsByLoginId(loginId).isPresent()) {
            throw new UserAlreadyExsistsException("User already exists");
        }
        return false;
    }

    @Override
    public Boolean existsByEmail(String email) throws UserAlreadyExsistsException {
        if (appUserRepo.customExistsByEmail(email).isPresent()) {
            throw new UserAlreadyExsistsException("User already exists");
        }
        return false;
    }

    @Override
    public String followHelper(FollowDto followDto, String userId) throws UsernameNotFoundException {
        AppUser user = appUserRepo.customExistsByLoginId(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found " + userId));
        AppUser follow = appUserRepo.customExistsByLoginId(followDto.getFollowId()).orElseThrow(
                () -> new UsernameNotFoundException("followId not found " + followDto.getFollowId()));
        if (followDto.getFollowType().equals(FollowDto.FollowType.FOLLOW)) {
            if (!user.getFollowing().add(follow.getLoginId())) {
                return "Already following the User";
            }
            follow.getFollowers().add(userId);
            appUserRepo.save(user);
            appUserRepo.save(follow);
            return "Successfully followed the User";
        } else if (followDto.getFollowType().equals(FollowDto.FollowType.UNFOLLOW)) {
            if (!follow.getFollowers().contains(user.getLoginId())) {
                return "first follow the user before unfollowing";
            }
            user.getFollowing().remove(follow.getLoginId());
            follow.getFollowers().remove(userId);
            appUserRepo.save(user);
            appUserRepo.save(follow);
            return "Successfully unfollowed the User";
        }
        return "Bad request";
    }

    @Override
    public OutputDto<Page<SearchAppUserResponseDto>> searchUser(String searchText, String principal, int page,
            int size) {
        OutputDto<Page<SearchAppUserResponseDto>> outputDto = new OutputDto<>();
        try {
            Page<SearchAppUserResponseDto> data = appUserRepo.searchByLoginId(searchText, PageRequest.of(page, size));
            data.getContent().forEach(user -> {
                if (user.getFollowers() != null) {
                    user.setYouFollow(user.getFollowers().contains(principal));
                    user.setFollowers(null);
                }
            });
            outputDto.setData(data);
            outputDto.setTypeOfPage(true);
            outputDto.setError(false);
            outputDto.setErrorMessage("");
            outputDto.setHttpStatus(HttpStatus.OK);
        } catch (Exception e) {
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

    @Override
    public OutputDto<AppUserResponseDto> findUserbyId(String userId, String principal) {
        OutputDto<AppUserResponseDto> outputDto = new OutputDto<>();
        try {
            AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
            AppUser appUser = appUserRepo.customExistsByLoginId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            appUserResponseDto.setLoginId(appUser.getLoginId());
            appUserResponseDto.setId(appUser.getId());
            appUserResponseDto.setFirstName(appUser.getFirstName());
            appUserResponseDto.setLastName(appUser.getLastName());
            appUserResponseDto.setImage(appUser.getImage());
            appUserResponseDto.setFollowerCount(appUser.getFollowers().size());
            appUserResponseDto.setFollowingCount(appUser.getFollowing().size());
            appUserResponseDto.setYouFollow(appUser.getFollowers().contains(principal));
            outputDto.setData(appUserResponseDto);
            outputDto.setError(false);
            outputDto.setErrorMessage("");
            outputDto.setHttpStatus(HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            log.error(e.getMessage());
            outputDto.setError(true);
            outputDto.setErrorMessage("user name not found");
            outputDto.setHttpStatus(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error(e.getMessage());
            outputDto.setError(true);
            outputDto.setErrorMessage("server error");
            outputDto.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return outputDto;
    }

}
