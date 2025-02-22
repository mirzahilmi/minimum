package id.my.mrz.hello.spring.domain.tag.dto;

import java.io.Serializable;

public final record TagResourceResponse(long id, String name) implements Serializable {}
