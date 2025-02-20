package id.my.mrz.hello.spring.domain.session.dto;

public final record SessionCreatedResponse(String accessToken, long expiresIn) {}
