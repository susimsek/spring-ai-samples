package io.github.susimsek.springaisamples.security;

import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PRIVATE_KEY_FOOTER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PRIVATE_KEY_HEADER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PUBLIC_KEY_FOOTER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PUBLIC_KEY_HEADER;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    private JwtProperties jwt;
    private JwsProperties jws;
    private EncryptionProperties encryption;
    private AdminProperties admin;
    private String contentSecurityPolicy;

    @Getter
    @Setter
    public static class JwtProperties {
        private boolean jweEnabled = true;
        String issuer;
        private String publicKey;
        private String privateKey;
        private Duration accessTokenExpiration;
        private Duration idTokenExpiration;
        private Duration refreshTokenExpiration;

        public String getFormattedPublicKey() {
            return PUBLIC_KEY_HEADER + publicKey + PUBLIC_KEY_FOOTER;
        }

        public String getFormattedPrivateKey() {
            return PRIVATE_KEY_HEADER + privateKey + PRIVATE_KEY_FOOTER;
        }
    }

    @Getter
    @Setter
    public static class JwsProperties {
        private String publicKey;
        private String privateKey;
        private Duration jwsExpiration;

        public String getFormattedPublicKey() {
            return PUBLIC_KEY_HEADER + publicKey + PUBLIC_KEY_FOOTER;
        }

        public String getFormattedPrivateKey() {
            return PRIVATE_KEY_HEADER + privateKey + PRIVATE_KEY_FOOTER;
        }
    }

    @Getter
    @Setter
    public static class EncryptionProperties {
        private String publicKey;
        private String privateKey;

        public String getFormattedPublicKey() {
            return PUBLIC_KEY_HEADER + publicKey + PUBLIC_KEY_FOOTER;
        }

        public String getFormattedPrivateKey() {
            return PRIVATE_KEY_HEADER + privateKey + PRIVATE_KEY_FOOTER;
        }
    }

    @Getter
    @Setter
    public static class AdminProperties {
        private String username;
        private String password;
        private String name;
        private String email;
    }
}