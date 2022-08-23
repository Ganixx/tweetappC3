package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tweetapp.entity.AppUser;
import com.tweetapp.exception.InvalidOtpException;
import com.tweetapp.model.AppUserRequestDto;
import com.tweetapp.model.AppUserResponseDto;
import com.tweetapp.model.OutputDto;
import com.tweetapp.model.SearchAppUserResponseDto;
import com.tweetapp.repo.AppUserRepo;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = TweetServiceTests.class)
 class AppUserServiceTests {

    @Mock
    private AppUserRepo appUserRepo;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testGetAllUsers() {

        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, null, null);
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        appUserResponseDto.setId("1");
        AppUserResponseDto appUserResponseDto1 = new AppUserResponseDto();
        appUserResponseDto1.setId("2");
        AppUserResponseDto appUserResponseDto2 = new AppUserResponseDto();
        appUserResponseDto2.setId("3");

        PageImpl<AppUserResponseDto> pageImpl = new PageImpl<>(
                Arrays.asList(appUserResponseDto, appUserResponseDto1, appUserResponseDto2));
        Mockito.when(appUserRepo.customFindAll(PageRequest.of(0, 5)))
                .thenReturn(pageImpl);

        OutputDto<Page<AppUserResponseDto>> outputDto = appUserService.getAllAppUsers(0, 5);
        assertEquals(3, outputDto.getData().getContent().size());
    }

    @Test
    void testGetAllUsersFail() {

        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, null, null);
        AppUserResponseDto appUserResponseDto = new AppUserResponseDto();
        appUserResponseDto.setId("1");
        AppUserResponseDto appUserResponseDto1 = new AppUserResponseDto();
        appUserResponseDto1.setId("2");
        AppUserResponseDto appUserResponseDto2 = new AppUserResponseDto();
        appUserResponseDto2.setId("3");

        PageImpl<AppUserResponseDto> pageImpl = new PageImpl<>(
                Arrays.asList(appUserResponseDto, appUserResponseDto1, appUserResponseDto2));
        Mockito.when(appUserRepo.customFindAll(PageRequest.of(0, 5)))
                .thenReturn(pageImpl);

        OutputDto<Page<AppUserResponseDto>> outputDto = appUserService.getAllAppUsers(0, 5);
        assertNotEquals(2, outputDto.getData().getContent().size());
    }

    @Test
    void testSearchUser() {
        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, null, null);
        SearchAppUserResponseDto appUserResponseDto = new SearchAppUserResponseDto();
        appUserResponseDto.setId("ramesh");
        SearchAppUserResponseDto appUserResponseDto1 = new SearchAppUserResponseDto();
        appUserResponseDto1.setId("ashok");
        SearchAppUserResponseDto appUserResponseDto2 = new SearchAppUserResponseDto();
        appUserResponseDto2.setId("asha");
        Page<SearchAppUserResponseDto> pageImpl = new PageImpl<>(
                Arrays.asList(appUserResponseDto1, appUserResponseDto2));
        Mockito.when(appUserRepo.searchByLoginId("ash", PageRequest.of(0, 5)))
                .thenReturn(pageImpl);
        OutputDto<Page<SearchAppUserResponseDto>> outputDto = appUserService.searchUser("ash", "ashok", 0, 5);
        assertEquals(2, outputDto.getData().getContent().size());
    }

    @Test
    void testCreateUser() throws Exception {

        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, passwordEncoder, otpService);
        AppUserRequestDto appUserRequestDto = new AppUserRequestDto();
        appUserRequestDto.setLoginId("ramesh");
        appUserRequestDto.setPassword("ramesh");
        appUserRequestDto.setOtp(999999);
        appUserRequestDto.setEmail("ramesh@gmail.com");

        AppUser createdAppUser = new AppUser();
        createdAppUser.setLoginId("ramesh");

        Mockito.when(otpService.verifyOtp("ramesh@gmail.com", 999999))
                .thenReturn(true);

        Mockito.when(passwordEncoder.encode("ramesh"))
                .thenReturn("ramesh");
        Mockito.when(appUserRepo.save(Mockito.any(AppUser.class)))
                .thenReturn(createdAppUser);

        AppUserResponseDto out = appUserService.createUser(appUserRequestDto);

        assertEquals("ramesh", out.getLoginId());

    }

    @Test
    void testCreateUserFail() throws Exception {

        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, passwordEncoder, otpService);
        AppUserRequestDto appUserRequestDto = new AppUserRequestDto();
        appUserRequestDto.setLoginId("ramesh");
        appUserRequestDto.setPassword("ramesh");
        appUserRequestDto.setOtp(999999);
        appUserRequestDto.setEmail("ramesh@gmail.com");

        Mockito.when(otpService.verifyOtp("ramesh@gmail.com", 999999))
                .thenReturn(Boolean.FALSE);

        assertThrows(InvalidOtpException.class, () -> appUserService.createUser(appUserRequestDto));
    }

    @Test
    void testFindUserById() {
        AppUserServiceImpl appUserService = new AppUserServiceImpl(appUserRepo, null, null);
        AppUser appUser = new AppUser();
        appUser.setLoginId("ramesh");
        appUser.setFollowers(new HashSet<>());
        appUser.setFollowing(new HashSet<>());

        Mockito.when(appUserRepo.customExistsByLoginId("ramesh"))
                .thenReturn(Optional.of(appUser));
        OutputDto<AppUserResponseDto> out = appUserService.findUserbyId("ramesh", "ashok");
        assertEquals(0, out.getData().getFollowerCount());
    }

}
