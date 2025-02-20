package id.my.mrz.hello.spring.domain.user.entity;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public final class Principal extends UsernamePasswordAuthenticationToken {
  private long id;

  public Principal(Object principal, Object credentials) {
    super(principal, credentials);
  }

  public Principal(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
