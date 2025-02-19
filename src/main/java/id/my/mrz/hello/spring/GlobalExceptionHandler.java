package id.my.mrz.hello.spring;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {
  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(ResourceViolationException.class)
  public ProblemDetail handleResourceViolationException(ResourceViolationException e) {
    // HACK: maybe better off put this in constants
    if (e.getTags().contains("duplicate"))
      return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

    if (e.getTags().contains("not_exist"))
      return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());

    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }
}
