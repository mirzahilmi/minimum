package id.my.mrz.minimum.domain.user.controller;

import id.my.mrz.minimum.domain.user.dto.UserResourceResponse;
import id.my.mrz.minimum.domain.user.dto.UserSignupRequest;
import id.my.mrz.minimum.domain.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API")
public final class UserController {
  private final IUserService service;

  public UserController(IUserService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UserResourceResponse> postSignUp(
      @RequestBody @Valid UserSignupRequest credential) {
    UserResourceResponse user = service.create(credential);
    return ResponseEntity.created(URI.create("/api/v1/users/" + user.getId())).body(user);
  }
}
