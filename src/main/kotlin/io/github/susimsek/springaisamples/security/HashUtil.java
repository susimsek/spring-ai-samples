package io.github.susimsek.springaisamples.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.codec.Hex;

@UtilityClass
public class HashUtil {

    public static final String ALGORITHM = "SHA-256";

    public String hashWithSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(encodedHash));
    }
}