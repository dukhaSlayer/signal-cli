package org.asamk.signal.manager.api;

public class CaptchaRequiredException extends Exception {

    private long nextVerificationAttemptMilliseconds;

    public CaptchaRequiredException(final long nextVerificationAttemptMilliseconds) {
        super("Captcha required");
        this.nextVerificationAttemptMilliseconds = nextVerificationAttemptMilliseconds;
    }

    public CaptchaRequiredException(final String message) {
        super(message);
    }

    public CaptchaRequiredException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public long getNextVerificationAttemptMilliseconds() {
        return nextVerificationAttemptMilliseconds;
    }
}
