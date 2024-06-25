package io.github.susimsek.springaisamples.security.encryption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionConstants {
    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----\n";
    public static final String PUBLIC_KEY_FOOTER = "\n-----END PUBLIC KEY-----";
    public static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----\n";
    public static final String PRIVATE_KEY_FOOTER = "\n-----END PRIVATE KEY-----";
    public static final String CHARSET = "UTF-8";
    public static final String CLAIM_NAME = "data";
}