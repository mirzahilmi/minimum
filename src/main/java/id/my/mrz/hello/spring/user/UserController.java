package id.my.mrz.hello.spring.user;

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
  public User postSignUp(@RequestBody UserSignupRequest credential) {
    User user = service.create(credential);
    // FIX: return dto response, dont expose sensitive data (password)
    return user;
  }
}
