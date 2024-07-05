package io.github.susimsek.springaisamples.service;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwksService {

    private final KeyPair jwtKeyPair;
    private final KeyPair jwsKeyPair;
    private final KeyPair jweKeyPair;

    @Cacheable("jwksCache")
    public Map<String, Object> getJwks() {
        RSAPublicKey jwsPublicKey = (RSAPublicKey) jwsKeyPair.getPublic();
        RSAKey jwsJwk = new RSAKey.Builder(jwsPublicKey)
            .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("1")
            .build();

        // JWT Key
        RSAPublicKey jwtPublicKey = (RSAPublicKey) jwtKeyPair.getPublic();
        RSAKey jwtJwk = new RSAKey.Builder(jwtPublicKey)
            .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("2")
            .build();

        // JWE Key
        RSAPublicKey jwePublicKey = (RSAPublicKey) jweKeyPair.getPublic();
        RSAKey jweJwk = new RSAKey.Builder(jwePublicKey)
            .keyUse(com.nimbusds.jose.jwk.KeyUse.ENCRYPTION)
            .algorithm(JWEAlgorithm.RSA_OAEP_256)
            .keyID("3")
            .build();

        // Create JWKSet with all keys
        JWKSet jwkSet = new JWKSet(List.of(jwsJwk, jwtJwk, jweJwk));
        return jwkSet.toJSONObject();
    }
}