package io.github.susimsek.springaisamples.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Decrypt request dto containing the payload to be decrypted")
public record DecryptRequest(
    @Schema(
        description = "The JWE token to be decrypted",
        example = "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ..."
    )
    @NotBlank(message = "{validation.field.notBlank}")
    String jweToken
) {
}