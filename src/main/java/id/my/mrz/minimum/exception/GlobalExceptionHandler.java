package id.my.mrz.minimum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {
  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(code = HttpStatus.NOT_FOUND)
  public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(ResourceViolationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST)
  public ProblemDetail handleResourceViolationException(ResourceViolationException e) {
    // HACK: maybe better off put this in constants
    if (e.getTags().contains("duplicate"))
      return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

    if (e.getTags().contains("not_exist"))
      return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());

    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public ProblemDetail fallback(Exception e) {
    e.printStackTrace();
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }
}
