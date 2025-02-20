package id.my.mrz.hello.spring.domain.tag.dto;

import java.io.Serializable;

public record TagResourceResponse(long id, String name) implements Serializable {}
