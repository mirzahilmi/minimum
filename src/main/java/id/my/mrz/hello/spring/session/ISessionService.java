package id.my.mrz.hello.spring.session;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public sealed interface ISessionService permits JwtService {
  SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception;

  Jws<Claims> parseJwt(String string);
}
