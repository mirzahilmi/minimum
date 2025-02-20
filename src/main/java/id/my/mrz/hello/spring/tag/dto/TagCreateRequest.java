package id.my.mrz.hello.spring.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagCreateRequest(
    @NotNull(message = "name is required") @NotBlank(message = "name cannot be blank")
        String name) {}
