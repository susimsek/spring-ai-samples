package io.github.susimsek.springaisamples.controller.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "security", description = "Security APIs")
@RequestMapping("/.well-known")
public class JwksController {

    private final KeyPair jwsKeyPair;

    @Operation(summary = "Get JWKS",
        description = "Provides the JSON Web Key Set (JWKS) containing the public key for JWS signature verification.")
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
                        }
                      ]
                    }
                    """)
            )),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable due to Circuit Breaker",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/jwks.json")
    @Cacheable("jwksCache")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) jwsKeyPair.getPublic();
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                .keyID("1")
                .build();
        JWKSet jwkSet = new JWKSet(jwk);
        return jwkSet.toJSONObject();
    }
}