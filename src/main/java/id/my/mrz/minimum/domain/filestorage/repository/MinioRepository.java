package id.my.mrz.minimum.domain.filestorage.repository;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Component(MinioRepository.BEAN_KEY)
public final class MinioRepository implements IFileStorageRepository {
    public static final String BEAN_KEY = "minio_repository";

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
