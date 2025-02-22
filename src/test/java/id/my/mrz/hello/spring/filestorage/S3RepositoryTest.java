package id.my.mrz.hello.spring.filestorage;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.mrz.hello.spring.Application;
import id.my.mrz.hello.spring.config.S3Config;
import id.my.mrz.hello.spring.domain.filestorage.repository.IFileStorageRepository;
import id.my.mrz.hello.spring.domain.filestorage.repository.S3Repository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Testcontainers
@SpringBootTest(classes = {S3Config.class, S3Repository.class})
class S3RepositoryTest {
  @Container
  static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2025-02-07T23-21-09Z");

  @Autowired
  @Qualifier(Application.Constant.S3_REPOSITORY)
  IFileStorageRepository repository;

  @Autowired S3Client client;

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
    HeadBucketRequest headBucketRequest = HeadBucketRequest.builder().bucket(bucket).build();
    try {
      client.headBucket(headBucketRequest);
    } catch (NoSuchBucketException ex) {
      CreateBucketRequest createBucketRequest =
          CreateBucketRequest.builder().bucket(bucket).build();
      client.createBucket(createBucketRequest);
    }
  }

  class File {
    private final String filename;
    private final InputStream stream;
    private final long size;
    private final String contentType;

    File(String filename, InputStream stream, long size, String contentType) {
      this.filename = filename;
      this.stream = stream;
      this.size = size;
      this.contentType = contentType;
    }

    public String getFilename() {
      return filename;
    }

    public InputStream getStream() {
      return stream;
    }

    public long getSize() {
      return size;
    }

    public String getContentType() {
      return contentType;
    }
  }

  @Test
  void uploadFile() {
    String content = "content";
    InputStream stream = new ByteArrayInputStream(content.getBytes());
    long size = content.length();
    File file = new File("mirza.txt", stream, size, "image/png");

    try {
      upload(file);
      stream.close();
    } catch (Exception ex) {
      assertThat(ex).isNull();
    }
  }

  @Test
  void getFileContentAndMatch() {
    String content = "content";
    byte[] contentBytes = content.getBytes();
    InputStream stream = new ByteArrayInputStream(contentBytes);
    File file = new File("mirza.txt", stream, content.length(), "image/png");

    byte[] savedBytes = {};
    try {
      String filename = upload(file);
      savedBytes = repository.getFileContent(filename).readAllBytes();
    } catch (Exception ex) {
      assertThat(ex).isNull();
    }

    assertThat(savedBytes).isNotEmpty().isEqualTo(contentBytes);
  }

  String upload(File file) throws Exception {
    return repository.uploadFile(
        file.getStream(), file.getFilename(), file.getSize(), file.getContentType());
  }
}
