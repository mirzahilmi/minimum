package id.my.mrz.hello.spring.minio;

import static org.assertj.core.api.Assertions.assertThatNoException;

import id.my.mrz.hello.spring.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = {MinioConfig.class, MinioRepository.class})
class MinioRepositoryTest {
  @Container
  static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2025-02-07T23-21-09Z");

  @Autowired MinioRepository repository;

  @Autowired MinioClient client;

  @Value("${minio.bucket}")
  String bucket;

  @DynamicPropertySource
  static void minioProperties(DynamicPropertyRegistry registry) {
    registry.add("minio.key.access", minio::getUserName);
    registry.add("minio.key.secret", minio::getPassword);
    registry.add("minio.endpoint", minio::getS3URL);
    registry.add("minio.bucket", () -> "bucket");
  }

  @BeforeEach
  void init() throws Exception {
    boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    if (!found) {
      client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
    }
  }

  @Test
  void uploadFile() {
    String content = "content";
    InputStream stream = new ByteArrayInputStream(content.getBytes());
    String filename = "mirza.png";
    long size = content.length();
    String contentType = "image/png";

    assertThatNoException()
        .isThrownBy(
            () -> {
              repository.uploadFile(stream, filename, size, contentType);
            });
  }
}
