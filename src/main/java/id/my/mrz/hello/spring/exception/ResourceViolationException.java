package id.my.mrz.hello.spring.exception;

public final class ResourceViolationException extends RuntimeException {
  public ResourceViolationException(String message) {
    super(message);
  }

  public ResourceViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}
