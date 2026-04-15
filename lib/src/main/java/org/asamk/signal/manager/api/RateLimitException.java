package org.asamk.signal.manager.api;

public class RateLimitException extends Exception {

    private final Long retryAfterMilliseconds;

    public RateLimitException(final Long retryAfterMilliseconds) {
        super("Rate limit");
        this.retryAfterMilliseconds = retryAfterMilliseconds;
    }

    public static RateLimitException from(org.whispersystems.signalservice.api.push.exceptions.RateLimitException e) {
        return new RateLimitException(e.getRetryAfterMilliseconds().orElse(null));
    }

    public Long getRetryAfterMilliseconds() {
        return retryAfterMilliseconds;
    }
}
