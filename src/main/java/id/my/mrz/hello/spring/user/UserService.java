package id.my.mrz.hello.spring.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
  private final IUserRepository repository;
  // Should use DI?
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  public UserService(IUserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));
  }

  public User create(UserSignupRequest credential) {
    String hashed = encoder.encode(credential.password());
    User user = new User(credential.username(), hashed);
    return repository.save(user);
  }
}
