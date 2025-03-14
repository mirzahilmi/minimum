package id.my.mrz.minimum.exception;

public final class AuthenticationViolationException extends RuntimeException {
    public AuthenticationViolationException(String message) {
        super(message);
    }
}
