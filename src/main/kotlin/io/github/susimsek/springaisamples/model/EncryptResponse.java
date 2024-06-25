package io.github.susimsek.springaisamples.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response model for encryption")
public record EncryptResponse(
    @Schema(description = "The encrypted data",
        example = "U2FsdGVkX1+JpQlJNVX1E6mFZgE8+HLzE1k5Z3sFqYc=")
    String encryptedData
) {
}