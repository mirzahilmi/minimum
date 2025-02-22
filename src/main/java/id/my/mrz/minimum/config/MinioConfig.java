package id.my.mrz.minimum.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
  @Value("${minio.key.access}")
  private String accessKey;

  @Value("${minio.key.secret}")
  private String secretKey;

  @Value("${minio.endpoint}")
  private String endpoint;

  @Bean
  public MinioClient minio() {
    return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
  }
}
