package io.github.susimsek.springaisamples.controller.security;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.susimsek.springaisamples.controller.auth.AuthController;
import io.github.susimsek.springaisamples.dto.DecryptRequest;
import io.github.susimsek.springaisamples.dto.EncryptRequest;
import io.github.susimsek.springaisamples.dto.LoginRequest;
import io.github.susimsek.springaisamples.dto.RefreshTokenRequest;
import io.github.susimsek.springaisamples.dto.SignatureRequest;
import io.github.susimsek.springaisamples.service.JwksService;
import io.github.susimsek.springaisamples.trace.Trace;
import io.github.susimsek.springaisamples.trace.TraceContext;
import io.github.susimsek.springaisamples.versioning.ApiInfo;
import io.github.susimsek.springaisamples.versioning.CurrentApiInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "security", description = "Security APIs")
@RequestMapping("/.well-known")
@Validated
@Slf4j
public class JwksController {

    private final JwksService jwksService;

    @Operation(summary = "Get JWKS",
        description = "Provides the JSON Web Key Set (JWKS) containing the public keys for JWS signature verification, "
            + "JWT authentication, and JWE encryption.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved JWKS",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(example = """
                    {
                      "keys": [
                        {
                          "kty": "RSA",
                          "e": "AQAB",
                          "use": "sig",
                          "kid": "1",
                          "alg": "RS256",
                          "n": "uSw55Oz_Q4GWJmnh5mJujvFwvoyPG5CBLEnRi2HzrBHMNtSt6avonGRo9x3GeO..."
                        },
                        {
                          "kty": "RSA",
                          "e": "AQAB",
                          "use": "sig",
                          "kid": "2",
                          "alg": "RS256",
                          "n": "uSw55Oz_Q4GWJmnh5mJujvFwvoyPG5CBLEnRi2HzrBHMNtSt6avonGRo9x3GeO..."
                        },
                        {
                          "kty": "RSA",
                          "e": "AQAB",
                          "use": "enc",
                          "kid": "3",
                          "alg": "RSA-OAEP-256",
                          "n": "pGa0Rtq2QZ3PE9F6ePQq2uQzTxJw7Q9H4D7k5cd92yJ9fKuwUzGk8OwOdRvM4k..."
                        }
                      ],
                      "_links": {
                        "self": {
                          "href": "http://localhost:8071/.well-known/jwks.json",
                          "type": "GET"
                        },
                        "token": {
                          "href": "http://localhost:8071/api/v1/auth/token",
                          "type": "POST"
                        },
                        "refresh": {
                           "href": "http://localhost:8071/.well-known/refresh",
                           "type": "POST"
                        },
                        "signature": {
                          "href": "http://localhost:8071/api/v1/security/sign",
                          "type": "POST"
                        },
                        "encrypt": {
                          "href": "http://localhost:8071/api/v1/security/encrypt",
                          "type": "POST"
                        },
                        "decrypt": {
                          "href": "http://localhost:8071/api/v1/security/decrypt",
                          "type": "POST"
                        }
                      }
                    }
                    """)
            )),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/jwks.json")
    public ResponseEntity<EntityModel<Map<String, Object>>> getJwks(
        @TraceContext Trace trace,
        @CurrentApiInfo ApiInfo apiInfo) {
        log.info("Trace: {}", trace);
        log.info("API Info: {}", apiInfo);

        Map<String, Object> jwks = jwksService.getJwks();

        EntityModel<Map<String, Object>> entityModel = EntityModel.of(jwks);
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(JwksController.class)
            .getJwks(null, null)).withSelfRel().withType(HttpMethod.GET.name()));
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class).login(
                new LoginRequest("username", "password")))
            .withRel("token").withType(HttpMethod.POST.name()));
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(AuthController.class)
                .refresh(new RefreshTokenRequest("refreshToken")))
            .withRel("refresh").withType(HttpMethod.POST.name()));
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(SignatureController.class)
                .createJws(new SignatureRequest("data")))
            .withRel("signature").withType(HttpMethod.POST.name()));
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(EncryptionController.class)
            .encryptData(new EncryptRequest("data"))).withRel("encrypt").withType(HttpMethod.POST.name()));
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(EncryptionController.class)
                .decryptData(new DecryptRequest("jweToken")))
            .withRel("decrypt").withType(HttpMethod.POST.name()));

        return ResponseEntity.ok(entityModel);
    }
}