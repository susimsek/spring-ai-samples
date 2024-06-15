package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.security.SecurityUtils;
import io.github.susimsek.springaisamples.security.Token;
import io.github.susimsek.springaisamples.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public Token authenticateUser(String username, String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(authentication);
    }

    public Token refreshToken(String refreshToken) {
        Authentication authentication = SecurityUtils.isJwtAuthentication()
            ? SecurityContextHolder.getContext().getAuthentication()
            : null;
        return tokenProvider.refreshToken(authentication, refreshToken);
    }
}