package id.my.mrz.hello.spring.photo;

public final record PhotoResponse(long id, String filename, byte[] data) {}
