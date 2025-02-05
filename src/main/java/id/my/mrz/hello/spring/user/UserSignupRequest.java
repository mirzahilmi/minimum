package id.my.mrz.hello.spring.user;

public final record UserSignupRequest(String username, String password, String repeatedPassword) {}
