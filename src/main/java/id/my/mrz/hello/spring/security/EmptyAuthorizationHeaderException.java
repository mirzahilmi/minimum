package id.my.mrz.hello.spring.security;

public class EmptyAuthorizationHeaderException extends RuntimeException {
  public EmptyAuthorizationHeaderException(String message) {
    super(message);
  }
}
