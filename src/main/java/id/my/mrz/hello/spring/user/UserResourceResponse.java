package id.my.mrz.hello.spring.user;

import java.io.Serializable;

final class UserResourceResponse implements Serializable {
  private final long id;
  private final String username;

  UserResourceResponse(long id, String username) {
    this.id = id;
    this.username = username;
  }

  long getId() {
    return id;
  }

  String getUsername() {
    return username;
  }
}
