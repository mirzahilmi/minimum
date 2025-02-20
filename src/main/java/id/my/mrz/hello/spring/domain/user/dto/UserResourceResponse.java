package id.my.mrz.hello.spring.domain.user.dto;

import java.io.Serializable;

public final class UserResourceResponse implements Serializable {
  private final long id;
  private final String username;

  public UserResourceResponse(long id, String username) {
    this.id = id;
    this.username = username;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }
}
