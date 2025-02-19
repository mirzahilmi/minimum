package id.my.mrz.hello.spring.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public final class UserSignupRequest implements Serializable {
  @NotNull(message = "username is required")
  @NotBlank(message = "username cannot be blank")
  private final String username;

  @NotNull(message = "password is required")
  @NotBlank(message = "password cannot be blank")
  @Size(min = 8, message = "password must be at least 8 characters long")
  private final String password;

  @NotNull(message = "password is required")
  private final String repeatedPassword;

  public UserSignupRequest(String username, String password, String repeatedPassword) {
    this.username = username;
    this.password = password;
    this.repeatedPassword = repeatedPassword;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRepeatedPassword() {
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
