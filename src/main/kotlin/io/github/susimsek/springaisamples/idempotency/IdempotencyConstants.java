package io.github.susimsek.springaisamples.idempotency;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdempotencyConstants {

    public static final String IDEMPOTENCY_HEADER_NAME = "X-Idempotency-Key";
}
