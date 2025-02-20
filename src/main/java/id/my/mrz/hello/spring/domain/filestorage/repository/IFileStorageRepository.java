package id.my.mrz.hello.spring.domain.filestorage.repository;

import java.io.InputStream;

public interface IFileStorageRepository {
  String uploadFile(InputStream stream, String filename, long size, String contentType)
      throws Exception;

  InputStream getFileContent(String filename) throws Exception;
}
