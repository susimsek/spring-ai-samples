package io.github.susimsek.springaisamples.security;

import java.util.Optional;

public interface TokenStore {
    void storeToken(TokenEntity token);

    Optional<TokenEntity> getToken(String token);

    void invalidateToken(String token);

    void invalidateAllTokens(String subject);
}