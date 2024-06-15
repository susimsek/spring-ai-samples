package io.github.susimsek.springaisamples.security;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenEntity {
    private String token;
    private String subject;
    private Instant expiresAt;
}
