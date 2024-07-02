package io.github.susimsek.springaisamples.controller.security;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.susimsek.springaisamples.model.DecryptRequest;
import io.github.susimsek.springaisamples.model.DecryptResponse;
import io.github.susimsek.springaisamples.model.EncryptRequest;
import io.github.susimsek.springaisamples.model.EncryptResponse;
import io.github.susimsek.springaisamples.service.EncryptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "security", description = "Security APIs")
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class EncryptionController {

    private final EncryptionService encryptionService;

    @Operation(summary = "Encrypt Data", description = "Encrypt the given data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully encrypted data",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = EncryptResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/encrypt")
    public ResponseEntity<EncryptResponse> encryptData(
        @Parameter(description = "Payload to be encrypted")
        @Valid @RequestBody EncryptRequest request) {
        String encryptedData = encryptionService.encryptData(request.data());
        EncryptResponse response = new EncryptResponse(encryptedData);
        response.add(WebMvcLinkBuilder.linkTo(methodOn(EncryptionController.class)
                .encryptData(request)).withSelfRel()
            .withType(HttpMethod.POST.name()));
        response.add(WebMvcLinkBuilder.linkTo(methodOn(EncryptionController.class)
                .decryptData(new DecryptRequest(encryptedData))).withRel("decrypt")
            .withType(HttpMethod.POST.name()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Decrypt Data", description = "Decrypt the given data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully decrypted data",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = DecryptResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/decrypt")
    public ResponseEntity<DecryptResponse> decryptData(
        @Parameter(description = "Payload to be decrypted")
        @Valid @RequestBody DecryptRequest request) {
        var decryptedData = encryptionService.decryptData(request.jweToken());
        DecryptResponse response = new DecryptResponse(decryptedData);
        response.add(WebMvcLinkBuilder.linkTo(methodOn(EncryptionController.class)
                .decryptData(request)).withSelfRel()
            .withType(HttpMethod.POST.name()));
        response.add(WebMvcLinkBuilder.linkTo(
            methodOn(EncryptionController.class).encryptData(new EncryptRequest(
                decryptedData))).withRel("encrypt")
            .withType(HttpMethod.POST.name()));
        return ResponseEntity.ok(response);
    }
}