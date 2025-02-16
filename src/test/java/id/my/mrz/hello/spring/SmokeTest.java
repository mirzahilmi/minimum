package id.my.mrz.hello.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class SmokeTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:17.3-alpine3.21");

  @Container
  static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2025-02-07T23-21-09Z");

  @DynamicPropertySource
  static void minioProperties(DynamicPropertyRegistry registry) {
    registry.add("minio.key.access", minio::getUserName);
    registry.add("minio.key.secret", minio::getPassword);
    registry.add("minio.endpoint", minio::getS3URL);
    registry.add("minio.bucket", () -> "bucket");
  }

  @Test
  void contextLoads() {}
}
