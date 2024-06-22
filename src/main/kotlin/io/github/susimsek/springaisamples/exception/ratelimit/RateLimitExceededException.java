package io.github.susimsek.springaisamples.exception.ratelimit;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    private final String rateLimiterName;
    private final int limitForPeriod;
    private final long availablePermissions;
    private final long resetTime;
    private final long waitTime;

    public RateLimitExceededException(
        String rateLimiterName, String message,
        int limitForPeriod, long availablePermissions, long resetTime, long waitTime) {
        super(message);
        this.rateLimiterName = rateLimiterName;
        this.limitForPeriod = limitForPeriod;
        this.availablePermissions = availablePermissions;
        this.resetTime = resetTime;
        this.waitTime = waitTime;
    }

}