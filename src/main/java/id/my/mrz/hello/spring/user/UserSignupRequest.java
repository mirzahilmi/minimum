package id.my.mrz.hello.spring.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

final class UserSignupRequest implements Serializable {
  @NotNull(message = "username is required")
  @NotBlank(message = "username cannot be blank")
  private final String username;

  @NotNull(message = "password is required")
  @NotBlank(message = "password cannot be blank")
  @Min(value = 8, message = "password must be at least 8 characters long")
  private final String password;

  @NotNull(message = "password is required")
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
