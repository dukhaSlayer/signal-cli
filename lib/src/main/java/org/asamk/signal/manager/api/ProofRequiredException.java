package org.asamk.signal.manager.api;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Thrown when rate-limited by the server and proof of humanity is required to continue messaging.
 */
public class ProofRequiredException extends Exception {

    private final String token;
    private final Set<Option> options;
    private final long retryAfterMilliseconds;

    public ProofRequiredException(final String token, final Set<Option> options, final long retryAfterMilliseconds) {
        super("Rate limit");
        this.token = token;
        this.options = options;
        this.retryAfterMilliseconds = retryAfterMilliseconds;
    }

    public static ProofRequiredException from(org.whispersystems.signalservice.api.push.exceptions.ProofRequiredException e) {
        return new ProofRequiredException(e.getToken(),
                e.getOptions().stream().map(Option::from).collect(Collectors.toSet()),
                e.getRetryAfterSeconds() * 1000L);
    }

    public String getToken() {
        return token;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public long getRetryAfterMilliseconds() {
        return retryAfterMilliseconds;
    }

    public enum Option {
        CAPTCHA,
        PUSH_CHALLENGE;

        static Option from(org.whispersystems.signalservice.api.push.exceptions.ProofRequiredException.Option option) {
            return switch (option) {
                case CAPTCHA -> CAPTCHA;
                case PUSH_CHALLENGE -> PUSH_CHALLENGE;
            };
        }
    }
}
