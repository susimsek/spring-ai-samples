package io.github.susimsek.springaisamples.repository;

import io.github.susimsek.springaisamples.entity.RefreshTokenEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    List<RefreshTokenEntity> findBySubject(String subject);

    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Transactional
    void deleteByToken(String token);

    @Modifying
    @Transactional
    void deleteBySubject(String subject);
}