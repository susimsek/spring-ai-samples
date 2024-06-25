package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for decryption")
public record DecryptResponse(
    @Schema(description = "The decrypted data",
        example = "{\"username\": \"admin\", \"password\": \"password\"}")
    Object decryptedData
) {
}