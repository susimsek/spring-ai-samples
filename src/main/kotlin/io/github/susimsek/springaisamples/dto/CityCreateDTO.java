package io.github.susimsek.springaisamples.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating a new city", example = """
        {
            "name": "Istanbul"
        }
    """)
public class CityCreateDTO {

    @NotBlank(message = "{validation.field.notBlank}")
    @Size(min = 3, max = 100, message = "{validation.field.size}")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\s]+$", message = "{validation.field.pattern}")
    @Schema(description = "The name of the city", example = "Istanbul")
    private String name;
}