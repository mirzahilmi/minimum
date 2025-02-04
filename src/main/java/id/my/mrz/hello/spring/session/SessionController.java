package id.my.mrz.hello.spring.session;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
  private final ISessionService service;

  public SessionController(ISessionService service) {
    this.service = service;
  }

  @PostMapping(value = "/api/v1/users/self/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SessionCreatedResponse> postSession(
      @RequestBody SessionCreateRequest attempt) throws Exception {
    return new ResponseEntity<>(service.createSession(attempt), HttpStatus.CREATED);
  }
}
