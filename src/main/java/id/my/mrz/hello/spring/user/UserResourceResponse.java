package id.my.mrz.hello.spring.user;

final class UserResourceResponse {
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
