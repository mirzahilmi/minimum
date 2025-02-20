package id.my.mrz.hello.spring.domain.user.service;

import id.my.mrz.hello.spring.domain.user.dto.UserResourceResponse;
import id.my.mrz.hello.spring.domain.user.dto.UserSignupRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public sealed interface IUserService extends UserDetailsService permits UserService {
  public UserResourceResponse create(UserSignupRequest credential);
}
