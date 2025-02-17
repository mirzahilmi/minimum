package id.my.mrz.hello.spring.user;

import java.io.Serializable;

final class UserSignupRequest implements Serializable {
  private final String username;
  private final String password;
  private final String repeatedPassword;

  UserSignupRequest(String username, String password, String repeatedPassword) {
    this.username = username;
    this.password = password;
    this.repeatedPassword = repeatedPassword;
  }

  String getUsername() {
    return username;
  }

  String getPassword() {
    return password;
  }

  String getRepeatedPassword() {
    return repeatedPassword;
  }

  @Override
  public String toString() {
    return "UserSignupRequest [username="
        + username
        + ", password="
        + password
        + ", repeatedPassword="
        + repeatedPassword
        + "]";
  }
}
