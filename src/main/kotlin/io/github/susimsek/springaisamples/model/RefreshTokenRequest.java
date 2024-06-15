package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token request model containing refresh token")
public record RefreshTokenRequest(
    @Schema(description = "Refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") 
    @NotBlank(message = "{validation.field.notBlank}") 
    String refreshToken
) {}