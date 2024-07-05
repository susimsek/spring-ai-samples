package io.github.susimsek.springaisamples.controller.simple;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.susimsek.springaisamples.dto.CityCreateDTO;
import io.github.susimsek.springaisamples.dto.CityDTO;
import io.github.susimsek.springaisamples.dto.CityUpdateDTO;
import io.github.susimsek.springaisamples.openapi.annotation.RequireJwsSignature;
import io.github.susimsek.springaisamples.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Validated
@Tag(name = "city", description = "City API")
@SecurityRequirement(name = "bearerAuth")
public class CityController {

    private final CityService cityService;

    @Operation(summary = "Get all cities", description = "Retrieve a list of all cities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<CollectionModel<CityDTO>> getAllCities() {
        List<CityDTO> cities = cityService.getAllCities();
        cities.forEach(this::addHateoasLinks);
        CollectionModel<CityDTO> cityCollectionModel = CollectionModel.of(cities,
            linkTo(methodOn(CityController.class).getAllCities()).withSelfRel());
        return ResponseEntity.ok(cityCollectionModel);
    }

    @Operation(summary = "Get city by ID", description = "Retrieve a city by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved city",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "City not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CityDTO> getCityById(
        @Parameter(description = "ID of the city to be retrieved")
        @PathVariable @Min(value = 1, message = "{validation.field.min}")
        @NotNull(message = "{validation.field.notNull}") Long id) {
        return cityService.getCityById(id)
            .map(city -> {
                addHateoasLinks(city);
                return ResponseEntity.ok(city);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @RequireJwsSignature
    @Operation(summary = "Create a new city", description = "Create a new city")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created city",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "City already exists",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<CityDTO> createCity(
        @Parameter(description = "Details of the new city to be created")
        @Valid @RequestBody CityCreateDTO cityCreateDTO) throws URISyntaxException {
        CityDTO createdCity = cityService.createCity(cityCreateDTO);
        addHateoasLinks(createdCity);
        return ResponseEntity
            .created(new URI(createdCity.getRequiredLink(IanaLinkRelations.SELF).getHref()))
            .body(createdCity);
    }

    @RequireJwsSignature
    @Operation(summary = "Update a city", description = "Update an existing city by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated city",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CityDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "City not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CityDTO> updateCity(
        @Parameter(description = "ID of the city to be updated")
        @PathVariable @Min(value = 1, message = "{validation.field.min}")
        @NotNull(message = "{validation.field.notNull}") Long id,
        @Parameter(description = "Updated details of the city")
        @Valid @RequestBody CityUpdateDTO cityUpdateDTO) {
        CityDTO updatedCity = cityService.updateCity(id, cityUpdateDTO);
        addHateoasLinks(updatedCity);
        return ResponseEntity.ok(updatedCity);
    }

    @Operation(summary = "Delete a city", description = "Delete a city by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted city"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "City not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(
        @Parameter(description = "ID of the city to be deleted")
        @PathVariable @Min(value = 1, message = "{validation.field.min}")
        @NotNull(message = "{validation.field.notNull}") Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    private void addHateoasLinks(CityDTO city) {
        city.add(linkTo(methodOn(CityController.class).getCityById(city.getId())).withSelfRel());
        city.add(linkTo(methodOn(CityController.class).updateCity(city.getId(), null)).withRel("update"));
        city.add(linkTo(methodOn(CityController.class).deleteCity(city.getId())).withRel("delete"));
    }
}