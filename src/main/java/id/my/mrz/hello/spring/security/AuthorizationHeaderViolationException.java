package id.my.mrz.hello.spring.security;

public final class AuthorizationHeaderViolationException extends RuntimeException {
  public AuthorizationHeaderViolationException(String message) {
    super(message);
  }
}
