package id.my.mrz.minimum.exception;

public final class AuthorizationHeaderViolationException extends RuntimeException {
    public AuthorizationHeaderViolationException(String message) {
        super(message);
    }
}
