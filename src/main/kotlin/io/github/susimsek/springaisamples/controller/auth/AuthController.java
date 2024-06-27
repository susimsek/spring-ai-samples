package io.github.susimsek.springaisamples.controller.auth;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.susimsek.springaisamples.model.DecryptRequest;
import io.github.susimsek.springaisamples.model.LoginRequest;
import io.github.susimsek.springaisamples.model.RefreshTokenRequest;
import io.github.susimsek.springaisamples.openapi.annotation.RequireJwsSignature;
import io.github.susimsek.springaisamples.security.Token;
import io.github.susimsek.springaisamples.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "auth", description = "Authentication APIs")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RequireJwsSignature
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Authenticate user",
        description = "Authenticate user and return encrypted access, ID, and refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
            content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Token.class)) }),
        @ApiResponse(responseCode = "400",
            description = "Invalid input, invalid encrypted data, or invalid JWS signature",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Invalid login credentials",
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
    @PostMapping("/token")
    public ResponseEntity<Token> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login request containing JWE token",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = DecryptRequest.class)))
        @Valid @RequestBody LoginRequest loginRequest) {

        Token tokenResponse = authenticationService.authenticateUser(
            loginRequest.username(), loginRequest.password());

        tokenResponse.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class)
                .login(loginRequest)).withSelfRel()
            .withType(HttpMethod.POST.name()));
        tokenResponse.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class)
                .refresh(new RefreshTokenRequest(tokenResponse.getRefreshToken()))).withRel("refresh_token")
            .withType(HttpMethod.POST.name()));

        return ResponseEntity.ok(tokenResponse);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Refresh token", description = "Refresh access, ID, and refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully refreshed token",
            content = { @Content(mediaType =  MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Token.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid input or invalid JWS signature",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token",
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
    @PostMapping("/refresh")
    public ResponseEntity<Token> refresh(
        @Parameter(description = "Refresh token request")
        @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {

        Token tokenResponse = authenticationService.refreshToken(refreshTokenRequest.refreshToken());

        tokenResponse.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class)
            .refresh(refreshTokenRequest)).withSelfRel());
        tokenResponse.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class)
            .login(new LoginRequest("username", "password"))).withRel("login"));

        return ResponseEntity.ok(tokenResponse);
    }
}