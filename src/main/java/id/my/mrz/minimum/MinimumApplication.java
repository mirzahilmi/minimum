package id.my.mrz.minimum;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
    info = @Info(title = "Minimum API", description = "Minimum OpenAPI v3.1.0 Document"))
public class MinimumApplication {
  public static void main(String[] args) {
    SpringApplication.run(MinimumApplication.class, args);
  }
}
