package id.my.mrz.hello.spring.session;

import id.my.mrz.hello.spring.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class JwtService implements ISessionService {
  private final IUserService userService;
  private final PasswordEncoder encoder;
  private final SecretKey secretKey;

  public JwtService(IUserService userService) {
    this.userService = userService;
    this.encoder = new BCryptPasswordEncoder(12);
    this.secretKey = Jwts.SIG.HS256.key().build();
  }

  @Override
  public SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception {
    UserDetails user = userService.loadUserByUsername(attempt.username());

    boolean isPasswordMatch = encoder.matches(attempt.password(), user.getPassword());
    if (!isPasswordMatch) throw new Exception();

    Instant expiration = Instant.now().plus(1, ChronoUnit.HOURS);
    String jwt =
        Jwts.builder()
            .subject(user.getUsername())
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact();

    return new SessionCreatedResponse(jwt, Duration.between(Instant.now(), expiration).toSeconds());
  }

  @Override
  public Jws<Claims> parseJwt(String jwt) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt);
  }
}
