package io.github.susimsek.springaisamples.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.exception.security.JwsEncodingException;
import io.github.susimsek.springaisamples.security.TokenProvider;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public String createJws(Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            return tokenProvider.createJws(jsonPayload);
        } catch (JsonProcessingException e) {
            throw new JwsEncodingException("Failed to encode JWS", e);
        }
    }

    public void validateJws(String jwsSignature, String payload) {
        String data = getData(payload);
        tokenProvider.validateJws(jwsSignature, data);
    }

    private String getData(String payload) {
        try {
            // Check if request body is JSON
            var jsonNode = objectMapper.readTree(payload);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            return payload;
        }
    }
}