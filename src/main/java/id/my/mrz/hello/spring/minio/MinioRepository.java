package id.my.mrz.hello.spring.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
final class MinioRepository {
  private final MinioClient client;

  @Value("${minio.bucket}")
  private String bucket;

  MinioRepository(MinioClient client) {
    this.client = client;
  }

  String uploadFile(InputStream stream, String filename, long size, String contentType)
      // HACK: bubble up-ed exception, looks ugly
      throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
    filename = String.format("%s-%s", UUID.randomUUID(), filename);

    PutObjectArgs args =
        PutObjectArgs.builder().bucket(bucket).object(filename).stream(stream, size, -1)
            .contentType(contentType)
            .build();
    client.putObject(args);

    return filename;
  }
}
