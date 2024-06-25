package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final TokenProvider tokenProvider;

    public String encryptData(Object data) {
        return tokenProvider.createJwe(data);
    }

    public Object decryptData(String encryptedData) {
        return tokenProvider.extractDataFromJwe(encryptedData);
    }
}
