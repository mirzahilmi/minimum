package id.my.mrz.minimum.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
  @Value("${minio.key.access}")
  private String accessKey;

  @Value("${minio.key.secret}")
  private String secretKey;

  @Value("${minio.endpoint}")
  private String endpoint;

  @Bean
  S3Client s3() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

    return S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.AP_SOUTHEAST_1)
        .credentialsProvider(credentialsProvider)
        .forcePathStyle(true)
        .build();
  }
}
