package id.my.mrz.hello.spring.domain.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public final record TagCreateRequest(
    @NotNull(message = "name is required") @NotBlank(message = "name cannot be blank") String name)
    implements Serializable {}
