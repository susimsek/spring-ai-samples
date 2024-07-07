package io.github.susimsek.springaisamples.security;

import io.github.susimsek.springaisamples.entity.RefreshTokenEntity;
import io.github.susimsek.springaisamples.repository.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseTokenStore implements TokenStore {

    private final RefreshTokenRepository tokenRepository;

    @Override
    public void storeToken(TokenEntity token) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
            .token(token.getToken())
            .subject(token.getSubject())
            .expiresAt(token.getExpiresAt())
            .build();
        tokenRepository.save(entity);
    }

    @Override
    public Optional<TokenEntity> getToken(String token) {
        return tokenRepository.findByToken(token)
            .map(entity -> new TokenEntity(entity.getToken(),
                entity.getSubject(), entity.getExpiresAt()));
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Override
    public void invalidateAllTokens(String subject) {
        tokenRepository.deleteBySubject(subject);
    }
}