package id.my.mrz.hello.spring.domain.filestorage.repository;

import id.my.mrz.hello.spring.Application;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component(Application.Constant.S3_REPOSITORY)
public final class S3Repository implements IFileStorageRepository {
  private final S3Client client;

  @Value("${minio.bucket}")
  private String bucket;

  public S3Repository(S3Client client) {
    this.client = client;
  }

  @Override
  public String uploadFile(InputStream stream, String filename, long size, String contentType)
      throws Exception {
    PutObjectRequest request =
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(filename)
            .contentType(contentType)
            .contentLength(size)
            .build();

    client.putObject(request, RequestBody.fromInputStream(stream, size));

    return filename;
  }

  @Override
  public InputStream getFileContent(String filename) throws Exception {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(filename).build();
    ResponseInputStream<GetObjectResponse> object =
        client.getObject(request, ResponseTransformer.toInputStream());
    return object;
  }
}
