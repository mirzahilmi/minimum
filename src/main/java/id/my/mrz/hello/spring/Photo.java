package id.my.mrz.hello.spring;

import jakarta.validation.constraints.NotEmpty;

public record Photo(@NotEmpty String id, String fileName) {
}
