package io.github.susimsek.springaisamples.security;

import java.util.Set;

public interface TokenStore {
    void storeToken(TokenEntity token);

    Set<TokenEntity> getTokens(String subject);

    void invalidateToken(String token);

    void invalidateAllTokens(String subject);
}