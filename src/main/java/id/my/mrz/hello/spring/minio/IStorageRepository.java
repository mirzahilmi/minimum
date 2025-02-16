package id.my.mrz.hello.spring.minio;

import java.io.InputStream;

interface IStorageRepository {
  String uploadFile(InputStream stream, String filename, long size, String contentType)
      throws Exception;

  InputStream getFileContent(String filename) throws Exception;
}
