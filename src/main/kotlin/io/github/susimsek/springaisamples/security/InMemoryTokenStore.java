package io.github.susimsek.springaisamples.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryTokenStore implements TokenStore {

    private final ConcurrentMap<String, Set<TokenEntity>> store = new ConcurrentHashMap<>();

    @Override
    public void storeToken(TokenEntity token) {
        store.computeIfAbsent(token.getSubject(), k -> Collections.synchronizedSet(new HashSet<>())).add(token);
    }

    @Override
    public Set<TokenEntity> getTokens(String subject) {
        return store.getOrDefault(subject, Collections.emptySet());
    }

    @Override
    public void invalidateToken(String token) {
        store.values().forEach(tokens -> tokens.removeIf(storedToken -> storedToken.getToken().equals(token)));
    }

    @Override
    public void invalidateAllTokens(String subject) {
        store.remove(subject);
    }
}