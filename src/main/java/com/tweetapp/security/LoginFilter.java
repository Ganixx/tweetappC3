package com.tweetapp.security;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader("username");
        String password = request.getHeader("password");

        var authenticated = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        String token = createJwtToken(authenticated);
        response.setHeader(HttpHeaders.AUTHORIZATION, token);
        // Cookie cookie = new Cookie("token", token);
        // cookie.setMaxAge(4 * 60 * 60 * 1000);
        // cookie.setHttpOnly(true);
        // cookie.setSecure(false);
        // cookie.setPath("/");
        // cookie.setDomain(".helpful-pothos-607833");
        // response.addCookie(cookie);
        response.setHeader("Set-Cookie", "token=" + token + "; Max-Age=14400000; HttpOnly; Secure; Path=/; SameSite=None;");
    }

    private String createJwtToken(Authentication authenticated) {
        User user = (User) authenticated.getPrincipal();
        var rolesString = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return jwtHelper.createToken(user.getUsername(), Map.of("roles", rolesString),
                Instant.now().plus(4, ChronoUnit.HOURS));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        boolean isLogin = HttpMethod.POST.matches(method) && uri.startsWith("/api/v1.0/tweets/login");
        return !isLogin;
    }
}
