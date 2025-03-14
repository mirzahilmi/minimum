package id.my.mrz.minimum.domain.tag.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final record TagCreateRequest(
    @NotNull(
        message = "name is required") @NotBlank(
            message = "name cannot be blank") String name)
    implements Serializable {
}
