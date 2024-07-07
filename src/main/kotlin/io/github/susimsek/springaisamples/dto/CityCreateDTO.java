package io.github.susimsek.springaisamples.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for creating a new city", example = """
        {
            "name": "Istanbul"
        }
    """)
public record CityCreateDTO(

    @NotBlank(message = "{validation.field.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.field.size}")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\s]+$", message = "{validation.field.pattern}")
    @Schema(description = "The name of the city", example = "Istanbul")
    String name
) {}