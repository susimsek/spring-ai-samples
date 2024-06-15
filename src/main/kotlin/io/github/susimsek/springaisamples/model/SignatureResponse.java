package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for JWS creation")
public record SignatureResponse(
    @Schema(description = "The generated JWS token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String jwsToken
) {
}