package io.github.susimsek.springaisamples.controller.security;

import io.github.susimsek.springaisamples.model.SignatureRequest;
import io.github.susimsek.springaisamples.model.SignatureResponse;
import io.github.susimsek.springaisamples.service.SignatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "security", description = "Security APIs")
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;

    @Operation(summary = "Create JWS", description = "Create a JSON Web Signature")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created JWS",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = SignatureResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Invalid JWS encoding",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProblemDetail.class))),
    })
    @PostMapping("/sign")
    public ResponseEntity<SignatureResponse> createJws(
        @Parameter(description = "Payload for JWS token generation")
        @RequestBody SignatureRequest signatureRequest) {
        String jws = signatureService.createJws(signatureRequest.payload());
        return ResponseEntity.ok(new SignatureResponse(jws));
    }
}