package id.my.mrz.minimum.domain.session.service;

import id.my.mrz.minimum.domain.session.dto.SessionCreateRequest;
import id.my.mrz.minimum.domain.session.dto.SessionCreatedResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public sealed interface ISessionService permits JwtService {
  SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception;

  Jws<Claims> parseJwt(String string);
}
