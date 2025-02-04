package id.my.mrz.hello.spring.session;

import id.my.mrz.hello.spring.user.IUserRepository;
import id.my.mrz.hello.spring.user.User;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements ISessionService {
  private final IUserRepository userRepository;
  private final PasswordEncoder encoder;
  private final SecretKey secretKey;

  public JwtService(IUserRepository userRepository, SecretKey secretKey) {
    this.userRepository = userRepository;
    this.encoder = new BCryptPasswordEncoder(12);
    this.secretKey = secretKey;
  }

  @Override
  public SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception {
    User user =
        userRepository
            .findByUsername(attempt.username())
            .orElseThrow(() -> new UsernameNotFoundException("username not found"));

    boolean isPasswordMatch = encoder.matches(attempt.password(), user.getPassword());
    if (!isPasswordMatch) throw new Exception();

    Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);
    String jwt =
        Jwts.builder()
            .subject(user.getId().toString())
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact();

    return new SessionCreatedResponse(jwt, Duration.between(Instant.now(), expiration).toSeconds());
  }
}
