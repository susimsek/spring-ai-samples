package io.github.susimsek.springaisamples.security.encryption;

import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.RSA_TRANSFORMATION;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EncryptionUtil {

    private final KeyPair encryptionKeyPair;

    public String encryptData(String data) throws GeneralSecurityException {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("Error occurred during encryption", e);
        }
    }

    public String decryptData(String encryptedData) throws GeneralSecurityException {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new GeneralSecurityException("Error occurred during decryption", e);
        }
    }

    private Cipher getCipher(int mode) throws GeneralSecurityException {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(Cipher.ENCRYPT_MODE, encryptionKeyPair.getPublic());
            } else if (mode == Cipher.DECRYPT_MODE) {
                cipher.init(Cipher.DECRYPT_MODE, encryptionKeyPair.getPrivate());
            }
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new GeneralSecurityException("Error initializing cipher", e);
        }
    }
}
