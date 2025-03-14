package id.my.mrz.minimum.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(
        code = HttpStatus.NOT_FOUND)
    public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ResourceViolationException.class)
    @ResponseStatus(
        code = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleResourceViolationException(ResourceViolationException e) {
        // HACK: maybe better off put this in constants
        if (e.getTags().contains("duplicate"))
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

        if (e.getTags().contains("not_exist"))
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AuthenticationViolationException.class)
    @ResponseStatus(
        code = HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleAuthenticationViolationException(
        AuthenticationViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(
        code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail fallback(Exception e) {
        e.printStackTrace();
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> violations = new HashMap<>();
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(error -> violations.put(
                error.getField(),
                error.getDefaultMessage()));
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("detail", violations);
        return problemDetail;
    }
}
