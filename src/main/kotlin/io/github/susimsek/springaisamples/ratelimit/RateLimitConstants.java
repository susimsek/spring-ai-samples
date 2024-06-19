package io.github.susimsek.springaisamples.ratelimit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RateLimitConstants {

    public static final String RATE_LIMIT_LIMIT_HEADER_NAME = "X-Rate-Limit-Limit";
    public static final String RATE_LIMIT_REMAINING_HEADER_NAME = "X-Rate-Limit-Remaining";
    public static final String RATE_LIMIT_RESET_HEADER_NAME = "X-Rate-Limit-Reset";
}
