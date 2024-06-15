package io.github.susimsek.springaisamples.security;

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
    private AdminProperties admin;

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
            return "-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----";
        }

        public String getFormattedPrivateKey() {
            return "-----BEGIN PRIVATE KEY-----\n" + privateKey + "\n-----END PRIVATE KEY-----";
        }
    }

    @Getter
    @Setter
    public static class JwsProperties {
        private String publicKey;
        private String privateKey;
        private Duration jwsExpiration;

        public String getFormattedPublicKey() {
            return "-----BEGIN PUBLIC KEY-----\n" + publicKey + "\n-----END PUBLIC KEY-----";
        }

        public String getFormattedPrivateKey() {
            return "-----BEGIN PRIVATE KEY-----\n" + privateKey + "\n-----END PRIVATE KEY-----";
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