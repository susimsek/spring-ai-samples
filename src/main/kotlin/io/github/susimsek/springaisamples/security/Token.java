package io.github.susimsek.springaisamples.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Schema(description = "JWT token response model", example = """
        {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "tokenType": "Bearer",
            "accessTokenExpiresIn": 3600,
            "idToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "idTokenExpiresIn": 900,
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshTokenExpiresIn": 86400,
            "_links": {
                "self": {
                    "href": "http://localhost:8071/api/v1/auth/token",
                    "type": "POST"
                },
                "refresh_token": {
                    "href": "http://localhost:8071/api/v1/auth/refresh",
                    "type": "POST"
                }
            }
        }
    """)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Token extends RepresentationModel<Token> {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access token expiration time in seconds", example = "3600")
    private long accessTokenExpiresIn;

    @Schema(description = "JWT ID token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String idToken;

    @Schema(description = "ID token expiration time in seconds", example = "900")
    private long idTokenExpiresIn;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Refresh token expiration time in seconds", example = "86400")
    private long refreshTokenExpiresIn;
}