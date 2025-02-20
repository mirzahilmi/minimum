package id.my.mrz.hello.spring.domain.filestorage.repository;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("miniorepository")
public final class MinioRepository implements IFileStorageRepository {
  private final MinioClient client;

  @Value("${minio.bucket}")
  private String bucket;

  MinioRepository(MinioClient client) {
    this.client = client;
  }

  @Override
  public String uploadFile(InputStream stream, String filename, long size, String contentType)
      throws Exception {
    filename = String.format("%s-%s", UUID.randomUUID(), filename);

    PutObjectArgs args =
        PutObjectArgs.builder().bucket(bucket).object(filename).stream(stream, size, -1)
            .contentType(contentType)
            .build();
    client.putObject(args);

    return filename;
  }

  @Override
  public InputStream getFileContent(String filename) throws Exception {
    return client.getObject(GetObjectArgs.builder().bucket(bucket).object(filename).build());
  }
}
