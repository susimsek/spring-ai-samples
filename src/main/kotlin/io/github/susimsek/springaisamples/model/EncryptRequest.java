package io.github.susimsek.springaisamples.model;

import io.github.susimsek.springaisamples.validation.NonEmptyString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Encrypt request model containing the payload to be encrypted")
public record EncryptRequest(
    @Schema(
        description = "The payload data to be encrypted",
        example = "{\"username\": \"admin\", \"password\": \"password\"}"
    )
    @NotNull(message = "{validation.field.notNull}")
    @NonEmptyString(message = "{validation.field.notBlank}")
    Object data
) {
}