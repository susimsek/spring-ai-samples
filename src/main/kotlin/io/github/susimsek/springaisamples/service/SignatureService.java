package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.security.TokenProvider;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final TokenProvider tokenProvider;
    private final JsonUtil jsonUtil;

    public String createJws(Object payload) {
        String jsonPayload = jsonUtil.convertObjectToString(payload);
        return tokenProvider.createJws(jsonPayload);
    }

    public void validateJws(String jwsSignature, String payload) {
        String data = jsonUtil.convertToJsonString(payload);
        tokenProvider.validateJws(jwsSignature, data);
    }
}