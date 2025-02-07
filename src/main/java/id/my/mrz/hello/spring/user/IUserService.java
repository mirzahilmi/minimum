package id.my.mrz.hello.spring.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public sealed interface IUserService extends UserDetailsService permits UserService {
  public UserResourceResponse create(UserSignupRequest credential);
}
