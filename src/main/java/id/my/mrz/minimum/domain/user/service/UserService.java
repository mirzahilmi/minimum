package id.my.mrz.minimum.domain.user.service;

import id.my.mrz.minimum.domain.user.dto.UserResourceResponse;
import id.my.mrz.minimum.domain.user.dto.UserSignupRequest;
import id.my.mrz.minimum.domain.user.entity.User;
import id.my.mrz.minimum.domain.user.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class UserService implements IUserService {
  private final IUserRepository repository;
  private final PasswordEncoder encoder;

  public UserService(IUserRepository repository) {
    this.repository = repository;
    this.encoder = new BCryptPasswordEncoder(12);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));
  }

  public UserResourceResponse create(UserSignupRequest credential) {
    String hashed = encoder.encode(credential.getPassword());
    User user = new User(credential.getUsername(), hashed);
    user = repository.save(user);
    return user.toUserResourceResponse();
  }
}
