package id.my.mrz.hello.spring.domain.session.controller;

import id.my.mrz.hello.spring.domain.session.dto.SessionCreateRequest;
import id.my.mrz.hello.spring.domain.session.dto.SessionCreatedResponse;
import id.my.mrz.hello.spring.domain.session.service.ISessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class SessionController {
  private final ISessionService service;

  public SessionController(ISessionService service) {
    this.service = service;
  }

  @PostMapping(value = "/api/v1/users/self/sessions")
  public ResponseEntity<SessionCreatedResponse> postSession(
      @RequestBody SessionCreateRequest attempt) throws Exception {
    SessionCreatedResponse response = service.createSession(attempt);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
}
