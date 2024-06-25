package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for encrypted data")
public record EncryptResponse(
    @Schema(description = "The generated JWE token", example = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ...")
    String jweToken
) {
}