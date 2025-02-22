package id.my.mrz.hello.spring.filestorage;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.mrz.hello.spring.Application;
import id.my.mrz.hello.spring.config.MinioConfig;
import id.my.mrz.hello.spring.domain.filestorage.repository.IFileStorageRepository;
import id.my.mrz.hello.spring.domain.filestorage.repository.MinioRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
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

@Testcontainers
@SpringBootTest(classes = {MinioConfig.class, MinioRepository.class})
class MinioRepositoryTest {
  @Container
  static MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2025-02-07T23-21-09Z");

  @Autowired
  @Qualifier(Application.Constant.MINIO_REPOSITORY)
  IFileStorageRepository repository;

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
