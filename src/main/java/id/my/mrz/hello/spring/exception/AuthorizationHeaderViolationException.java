package id.my.mrz.hello.spring.exception;

public final class AuthorizationHeaderViolationException extends RuntimeException {
  public AuthorizationHeaderViolationException(String message) {
    super(message);
  }
}
