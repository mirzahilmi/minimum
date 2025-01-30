package id.my.mrz.hello.spring.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
  public User create(UserSignupRequest credential);
}
