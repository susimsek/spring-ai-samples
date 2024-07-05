package io.github.susimsek.springaisamples.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Represents a city with its details and HATEOAS links", example = """
        {
            "id": 1,
            "name": "Istanbul",
            "createdAt": "2023-01-01T12:00:00Z",
            "createdBy": "admin",
            "updatedAt": "2023-01-02T12:00:00Z",
            "updatedBy": "admin",
            "_links": {
                "self": {
                    "href": "http://localhost:8080/api/v1/cities/1",
                    "type": "GET"
                },
                "update": {
                    "href": "http://localhost:8080/api/v1/cities/1",
                    "type": "PUT"
                },
                "delete": {
                    "href": "http://localhost:8080/api/v1/cities/1",
                    "type": "DELETE"
                }
            }
        }
    """)
public class CityDTO extends RepresentationModel<CityDTO> {

    @Schema(description = "The unique identifier of the city", example = "1")
    private Long id;

    @Schema(description = "The name of the city", example = "Istanbul")
    private String name;

    @Schema(description = "The date and time when the city was created", example = "2023-01-01T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "The user who created the city", example = "admin")
    private String createdBy;

    @Schema(description = "The date and time when the city was last updated", example = "2023-01-02T12:00:00Z")
    private Instant updatedAt;

    @Schema(description = "The user who last updated the city", example = "admin")
    private String updatedBy;
}