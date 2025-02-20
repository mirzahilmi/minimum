package id.my.mrz.hello.spring.domain.session.service;

import id.my.mrz.hello.spring.domain.session.dto.SessionCreateRequest;
import id.my.mrz.hello.spring.domain.session.dto.SessionCreatedResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public sealed interface ISessionService permits JwtService {
  SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception;

  Jws<Claims> parseJwt(String string);
}
