package id.my.mrz.minimum.domain.session.controller;

import id.my.mrz.minimum.domain.session.dto.SessionCreateRequest;
import id.my.mrz.minimum.domain.session.dto.SessionCreatedResponse;
import id.my.mrz.minimum.domain.session.service.ISessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User API", description = "User resource actions including authentication")
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
