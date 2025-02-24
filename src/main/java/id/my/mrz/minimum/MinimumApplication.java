package id.my.mrz.minimum;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
    info =
        @Info(
            title = "Minimum API",
            description = "Minimum OpenAPI v3.1.0 Document",
            contact = @Contact(name = "Mirza", url = "https://github.com/mirzahilmi")),
    servers = {@Server(url = "http://localhost:8080", description = "Local Environment")})
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer")
public class MinimumApplication {
  public static void main(String[] args) {
    SpringApplication.run(MinimumApplication.class, args);
  }
}
