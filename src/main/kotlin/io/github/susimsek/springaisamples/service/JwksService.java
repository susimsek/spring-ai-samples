package io.github.susimsek.springaisamples.service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.github.susimsek.springaisamples.controller.auth.AuthController;
import io.github.susimsek.springaisamples.controller.security.EncryptionController;
import io.github.susimsek.springaisamples.controller.security.JwksController;
import io.github.susimsek.springaisamples.controller.security.SignatureController;
import io.github.susimsek.springaisamples.model.DecryptRequest;
import io.github.susimsek.springaisamples.model.EncryptRequest;
import io.github.susimsek.springaisamples.model.LoginRequest;
import io.github.susimsek.springaisamples.model.RefreshTokenRequest;
import io.github.susimsek.springaisamples.model.SignatureRequest;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwksService {

    private final KeyPair jwtKeyPair;
    private final KeyPair jwsKeyPair;
    private final KeyPair jweKeyPair;

    @Cacheable("jwksCache")
    public EntityModel<Map<String, Object>> getJwks() {
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
        Map<String, Object> jwks =  jwkSet.toJSONObject();

        EntityModel<Map<String, Object>> entityModel = EntityModel.of(jwks);
        entityModel.add(WebMvcLinkBuilder.linkTo(methodOn(JwksController.class)
            .getJwks(null ,null)).withSelfRel().withType(HttpMethod.GET.name()));
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
        return entityModel;
    }
}