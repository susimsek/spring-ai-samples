package io.github.susimsek.springaisamples.model;

import io.github.susimsek.springaisamples.validation.NonEmptyString;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Signature request model containing the payload for JWS token generation")
public record SignatureRequest(
    @Schema(
        description = "The payload data to be signed",
        example = "{\"username\": \"admin\", \"password\": \"password\"}"
    )
    @NotNull(message = "{validation.field.notNull}")
    @NonEmptyString(message = "{validation.field.notBlank}")
    Object data
) {
}