package io.github.susimsek.springaisamples.security;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.github.susimsek.springaisamples.exception.encryption.BadJweException;
import io.github.susimsek.springaisamples.exception.encryption.JweEncodingException;
import io.github.susimsek.springaisamples.exception.security.BadJwsException;
import io.github.susimsek.springaisamples.exception.security.JwsEncodingException;
import io.github.susimsek.springaisamples.security.encryption.EncryptionConstants;
import io.github.susimsek.springaisamples.security.signature.SignatureConstants;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@RequiredArgsConstructor
public class TokenProvider {

    private final JwtDecoder jwtDecoder;
    private final JwtEncoder jwtEncoder;
    private final KeyPair jwtKeyPair;
    private final SecurityProperties securityProperties;
    private final TokenStore tokenStore;
    private final KeyPair jwsKeyPair;
    private final KeyPair jweKeyPair;

    public TokenProvider(JwtEncoder jwtEncoder,
                         KeyPair jwtKeyPair,
                         SecurityProperties securityProperties,
                         TokenStore tokenStore,
                         KeyPair jwsKeyPair,
                         KeyPair jweKeyPair) {
        this.jwtEncoder = jwtEncoder;
        this.jwtKeyPair = jwtKeyPair;
        this.securityProperties = securityProperties;
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtKeyPair.getPublic()).build();
        this.tokenStore = tokenStore;
        this.jwsKeyPair = jwsKeyPair;
        this.jweKeyPair = jweKeyPair;
    }

    public Token createToken(Authentication authentication) {
        boolean jweEnabled = securityProperties.getJwt().isJweEnabled();
        JwtClaimsSet accessTokenClaimsSet = buildJwtClaimsSet(authentication,
            securityProperties.getJwt().getAccessTokenExpiration());
        String accessToken = encodeJwt(accessTokenClaimsSet);
        String finalAccessToken = jweEnabled ? encryptJwtToJwe(accessToken) : accessToken;

        JwtClaimsSet idTokenClaimsSet = buildIdTokenClaimsSet(authentication,
            securityProperties.getJwt().getIdTokenExpiration());
        String idToken = encodeJwt(idTokenClaimsSet);
        String finalIdToken = jweEnabled ? encryptJwtToJwe(idToken) : idToken;

        JwtClaimsSet refreshTokenClaimsSet = buildRefreshTokenClaimsSet(authentication,
            securityProperties.getJwt().getRefreshTokenExpiration());
        String refreshToken = encodeJwt(refreshTokenClaimsSet);
        String finalRefreshToken = jweEnabled ? encryptJwtToJwe(refreshToken) : refreshToken;

        TokenEntity tokenEntity = new TokenEntity(finalRefreshToken, authentication.getName(),
            Instant.now().plus(securityProperties.getJwt().getRefreshTokenExpiration()));
        tokenStore.storeToken(tokenEntity);

        return new Token(
            finalAccessToken,
            OAuth2AccessToken.TokenType.BEARER.getValue(),
            securityProperties.getJwt().getAccessTokenExpiration().toSeconds(),
            finalIdToken,
            securityProperties.getJwt().getIdTokenExpiration().toSeconds(),
            finalRefreshToken,
            securityProperties.getJwt().getRefreshTokenExpiration().toSeconds()
        );
    }

    public Token refreshToken(Authentication authentication, String refreshToken) {
        String subject = getSubjectFromJwt(refreshToken);
        if (!isValidRefreshToken(refreshToken, subject)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (authentication == null || !authentication.isAuthenticated()
            || !authentication.getName().equals(subject)) {
            throw new BadCredentialsException("Invalid refresh token or subject mismatch");
        }
        invalidateToken(refreshToken);
        return createToken(authentication);
    }

    public String createJws(String data) {
        try {
            String hashedData = HashUtil.hashWithSHA256(data);
            var jwsClaimsSet = buildJwsClaimsSet(hashedData,
                securityProperties.getJws().getJwsExpiration());
            SignedJWT signedJwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                jwsClaimsSet
            );
            signedJwt.sign(new RSASSASigner(jwsKeyPair.getPrivate()));
            return signedJwt.serialize();
        } catch (JOSEException | NoSuchAlgorithmException e) {
            throw new JwsEncodingException("Failed to encrypt JWS", e);
        }
    }

    public String createJwe(Object data) {
        try {
            var jwsClaimsSet = buildJweClaimsSet(data,
                securityProperties.getJwe().getJweExpiration());
            SignedJWT signedJwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                jwsClaimsSet
            );
            signedJwt.sign(new RSASSASigner(jweKeyPair.getPrivate()));
            JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build(),
                new Payload(signedJwt));

            jweObject.encrypt(new RSAEncrypter((RSAPublicKey) jweKeyPair.getPublic()));
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new JweEncodingException("Failed to encrypt JWE", e);
        }
    }


    private JwtClaimsSet buildJwtClaimsSet(Authentication authentication, Duration tokenExpiration) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
            .subject(authentication.getName())
            .issuer(securityProperties.getJwt().getIssuer())
            .issuedAt(now)
            .expiresAt(now.plus(tokenExpiration))
            .id(UUID.randomUUID().toString());
        claimsBuilder.claim(AuthoritiesConstants.CLAIM_NAME,
            authentication.getAuthorities().stream()
                .map(Object::toString)
                .toList());
        return claimsBuilder.build();
    }

    private JwtClaimsSet buildIdTokenClaimsSet(Authentication authentication, Duration tokenExpiration) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
            .subject(authentication.getName())
            .issuer(securityProperties.getJwt().getIssuer())
            .issuedAt(now)
            .expiresAt(now.plus(tokenExpiration))
            .id(UUID.randomUUID().toString())
            .claim("name", securityProperties.getAdmin().getName())
            .claim("email", securityProperties.getAdmin().getEmail())
            .build();
    }

    private JwtClaimsSet buildRefreshTokenClaimsSet(Authentication authentication, Duration tokenExpiration) {
        Instant now = Instant.now();
        return JwtClaimsSet.builder()
            .subject(authentication.getName())
            .issuer(securityProperties.getJwt().getIssuer())
            .issuedAt(now)
            .expiresAt(now.plus(tokenExpiration))
            .id(UUID.randomUUID().toString())
            .build();
    }

    private com.nimbusds.jwt.JWTClaimsSet buildJwsClaimsSet(String data, Duration tokenExpiration) {
        Instant now = Instant.now();
        return new JWTClaimsSet.Builder()
            .issuer(securityProperties.getJwt().getIssuer())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plus(tokenExpiration)))
            .claim(SignatureConstants.CLAIM_NAME, data)
            .jwtID(UUID.randomUUID().toString())
            .build();
    }

    private com.nimbusds.jwt.JWTClaimsSet buildJweClaimsSet(Object data, Duration tokenExpiration) {
        Instant now = Instant.now();
        return new JWTClaimsSet.Builder()
            .issuer(securityProperties.getJwt().getIssuer())
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plus(tokenExpiration)))
            .claim(EncryptionConstants.CLAIM_NAME, data)
            .jwtID(UUID.randomUUID().toString())
            .build();
    }

    private String encodeJwt(JwtClaimsSet claimsSet) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    private String encryptJwtToJwe(String jwtToken) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(jwtToken);

            JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build(),
                new Payload(signedJwt));

            jweObject.encrypt(new RSAEncrypter((RSAPublicKey) jwtKeyPair.getPublic()));

            return jweObject.serialize();
        } catch (JOSEException | ParseException e) {
            throw new JwtEncodingException("Failed to encrypt JWT to JWE", e);
        }
    }

    public String decryptJweToJwt(String jweToken) {
        try {
            JWEObject jweObject = JWEObject.parse(jweToken);

            // Decrypt JWE token
            jweObject.decrypt(new RSADecrypter(jwtKeyPair.getPrivate()));

            // Extract the payload (JWT)
            SignedJWT signedJwt = jweObject.getPayload().toSignedJWT();
            if (signedJwt == null) {
                throw new BadJwtException("Invalid JWE token");
            }

            // Return the JWT as a string
            return signedJwt.serialize();
        } catch (JOSEException | ParseException e) {
            throw new BadJwtException("Failed to decrypt JWE token", e);
        }
    }

    public Jwt parseToken(String token) {
        boolean jweEnabled = securityProperties.getJwt().isJweEnabled();
        String jwt = token;
        if (jweEnabled && isJweToken(token)) {
            jwt = decryptJweToJwt(token);
        }
        return jwtDecoder.decode(jwt);
    }

    public boolean isJwtToken(String token) {
        return token.split("\\.").length == 3;
    }

    public boolean isJweToken(String token) {
        return token.split("\\.").length == 5;
    }

    public boolean isJwsToken(String token) {
        return token.split("\\.").length == 3;
    }

    public String getSubjectFromJwt(String token) {
        Jwt jwt = parseToken(token);
        return jwt.getSubject();
    }

    public boolean isValidRefreshToken(String token, String subject) {
        Set<TokenEntity> tokens = tokenStore.getTokens(subject);
        return tokens.stream().anyMatch(t -> t.getToken().equals(token));
    }

    public void invalidateToken(String token) {
        tokenStore.invalidateToken(token);
    }

    public void invalidateAllTokens(String subject) {
        tokenStore.invalidateAllTokens(subject);
    }

    public String extractDataFromJws(String jwsToken) {
        try {
            SignedJWT signedJwt = SignedJWT.parse(jwsToken);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jwsKeyPair.getPublic());

            if (!signedJwt.verify(verifier)) {
                throw new BadJwsException("JWS signature verification failed");
            }

            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
            Instant now = Instant.now();

            if (claims.getExpirationTime().before(Date.from(now))) {
                throw new BadJwsException("JWS token is expired");
            }

            return claims.getStringClaim(SignatureConstants.CLAIM_NAME);
        } catch (ParseException | JOSEException e) {
            throw new BadJwsException("Failed to extract data from JWS", e);
        }
    }

    public Object extractDataFromJwe(String jweToken) {
        try {
            JWEObject jweObject = JWEObject.parse(jweToken);

            // Decrypt JWE token
            jweObject.decrypt(new RSADecrypter(jweKeyPair.getPrivate()));

            // Extract the payload (JWE)
            SignedJWT signedJwt = jweObject.getPayload().toSignedJWT();
            if (signedJwt == null) {
                throw new BadJweException("Invalid JWE token");
            }
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) jweKeyPair.getPublic());

            if (!signedJwt.verify(verifier)) {
                throw new BadJweException("JWE signature verification failed");
            }

            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
            Instant now = Instant.now();

            if (claims.getExpirationTime().before(Date.from(now))) {
                throw new BadJweException("JWE token is expired");
            }

            return claims.getClaim(EncryptionConstants.CLAIM_NAME);
        } catch (ParseException | JOSEException e) {
            throw new BadJweException("Failed to extract data from JWE", e);
        }
    }

    public void validateJws(String jwsSignature, String data) {
        try {
            String extractDataFromJws = extractDataFromJws(jwsSignature);
            String hashedPayload = HashUtil.hashWithSHA256(data);
            if (!Objects.equals(extractDataFromJws, hashedPayload)) {
                throw new BadJwsException("Payload hash does not match");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new BadJwsException("Error while hashing the payload", e);
        }
    }
}