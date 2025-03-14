package id.my.mrz.minimum.config;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Component
@OpenAPIDefinition(
    info = @Info(
        title = "Minimum API",
        description = "Minimum OpenAPI v3.1.0 Document",
        contact = @Contact(
            name = "GitHub",
            url = "https://github.com/mirzahilmi")),
    servers = {@Server(
        url = "http://localhost:8080",
        description = "Local Environment")})
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER)
public class Springdoc {
}
