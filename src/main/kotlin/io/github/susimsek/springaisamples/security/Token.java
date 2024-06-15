package io.github.susimsek.springaisamples.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT token response model")
public record Token(
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,
    @Schema(description = "Token type", example = "Bearer") String tokenType,
    @Schema(description = "Access token expiration time in seconds", example = "3600") long accessTokenExpiresIn,
    @Schema(description = "JWT ID token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String idToken,
    @Schema(description = "ID token expiration time in seconds", example = "900") long idTokenExpiresIn,
    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String refreshToken,
    @Schema(description = "Refresh token expiration time in seconds", example = "86400") long refreshTokenExpiresIn
) {}