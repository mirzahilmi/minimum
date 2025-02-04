package id.my.mrz.hello.spring.session;

public interface ISessionService {
  SessionCreatedResponse createSession(SessionCreateRequest attempt) throws Exception;
}
