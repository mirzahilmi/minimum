package id.my.mrz.minimum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MinimumApplication {
  public static void main(String[] args) {
    SpringApplication.run(MinimumApplication.class, args);
  }

  public static final class Constant {
    public static final String MINIO_REPOSITORY = "minio_repository";
    public static final String S3_REPOSITORY = "s3_repository";
  }
}
