package io.github.susimsek.springaisamples.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request dto containing username and password")
public record LoginRequest(
    @Schema(description = "The username of the user", example = "admin")
    @NotBlank(message = "{validation.field.notBlank}")
    String username,

    @Schema(description = "The password of the user", example = "password")
    @NotBlank(message = "{validation.field.notBlank}")
    String password
) {
}