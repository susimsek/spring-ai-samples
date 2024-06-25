package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.security.TokenProvider;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final TokenProvider tokenProvider;
    private final JsonUtil jsonUtil;

    public String encryptData(String data) {
        return tokenProvider.createJwe(data);
    }

    public String encryptDataAsObject(Object data) {
        var jsonData = jsonUtil.convertObjectToString(data);
        return encryptData(jsonData);
    }

    public String decryptData(String encryptedData) {
        return tokenProvider.extractDataFromJwe(encryptedData);
    }

    public Object decryptDataAsObject(String encryptedData) {
        var data = decryptData(encryptedData);
        return jsonUtil.convertToJsonObject(data);
    }
}
