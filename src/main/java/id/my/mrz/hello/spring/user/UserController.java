package id.my.mrz.hello.spring.user;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public final class UserController {
  private final IUserService service;

  public UserController(IUserService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UserResourceResponse> postSignUp(
      @RequestBody UserSignupRequest credential) {
    UserResourceResponse user = service.create(credential);
    return ResponseEntity.created(URI.create("/api/v1/users/" + user.id())).body(user);
  }
}
