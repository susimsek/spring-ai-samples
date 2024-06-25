package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.exception.encryption.BadEncryptionException;
import io.github.susimsek.springaisamples.exception.encryption.EncryptionEncodingException;
import io.github.susimsek.springaisamples.security.encryption.EncryptionUtil;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EncryptionService {

    private final EncryptionUtil encryptionUtil;
    private final JsonUtil jsonUtil;

    public String encryptData(String data) {
        try {
            return encryptionUtil.encryptData(data);
        } catch (GeneralSecurityException e) {
            throw new EncryptionEncodingException("Failed to encrypt response body", e);
        }
    }

    public String encryptDataAsObject(Object data) {
        var jsonData = jsonUtil.convertObjectToString(data);
        return encryptData(jsonData);
    }

    public String decryptData(String encryptedData) {
        try {
            return encryptionUtil.decryptData(encryptedData);
        } catch (GeneralSecurityException e) {
            throw  new BadEncryptionException("Invalid encrypted data. Please check your input.");
        }
    }

    public Object decryptDataAsObject(String encryptedData) {
        var data = decryptData(encryptedData);
        return jsonUtil.convertToJsonObject(data);
    }
}
