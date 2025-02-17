package id.my.mrz.hello.spring.filestorage;

import java.io.InputStream;

public interface IFileStorageRepository {
  String uploadFile(InputStream stream, String filename, long size, String contentType)
      throws Exception;

  InputStream getFileContent(String filename) throws Exception;
}
