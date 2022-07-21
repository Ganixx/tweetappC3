package com.tweetapp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tweetapp.entity.AppUser;
import com.tweetapp.model.Roles;
import com.tweetapp.repo.AppUserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepo appUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appuser = appUserRepo.customFindByLoginId(username);
        if (appuser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Roles> tempRoles = appuser.getRole();
        Set<GrantedAuthority> ga = new HashSet<>();
        for (Roles ttrole : tempRoles) {
            ga.add(new SimpleGrantedAuthority(ttrole.name()));
        }
        return new org.springframework.security.core.userdetails.User(appuser.getLoginId(), appuser.getPassword(), ga);

    }

}
