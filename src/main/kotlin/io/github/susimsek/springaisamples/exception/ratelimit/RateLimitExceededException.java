package io.github.susimsek.springaisamples.exception.ratelimit;

public class RateLimitExceededException extends RuntimeException {

    private final int limitForPeriod;
    private final long availablePermissions;
    private final long resetTime;

    public RateLimitExceededException(String message, int limitForPeriod, long availablePermissions, long resetTime) {
        super(message);
        this.limitForPeriod = limitForPeriod;
        this.availablePermissions = availablePermissions;
        this.resetTime = resetTime;
    }

    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    public long getAvailablePermissions() {
        return availablePermissions;
    }

    public long getResetTime() {
        return resetTime;
    }
}