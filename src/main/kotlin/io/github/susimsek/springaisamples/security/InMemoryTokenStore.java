package io.github.susimsek.springaisamples.security;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTokenStore implements TokenStore {

    private final ConcurrentMap<String, TokenEntity> store = new ConcurrentHashMap<>();

    @Override
    public void storeToken(TokenEntity token) {
        store.put(token.getToken(), token);
    }

    @Override
    public Optional<TokenEntity> getToken(String token) {
        return Optional.ofNullable(store.get(token));
    }

    @Override
    public void invalidateToken(String token) {
        store.remove(token);
    }

    @Override
    public void invalidateAllTokens(String subject) {
        store.entrySet().removeIf(entry -> entry.getValue().getSubject().equals(subject));
    }
}