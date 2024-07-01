package io.github.susimsek.springaisamples.security;

import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PRIVATE_KEY_FOOTER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PRIVATE_KEY_HEADER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PUBLIC_KEY_FOOTER;
import static io.github.susimsek.springaisamples.security.encryption.EncryptionConstants.PUBLIC_KEY_HEADER;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {
    @Valid
    private JwtProperties jwt;
    @Valid
    private JwsProperties jws;
    @Valid
    private JweProperties jwe;
    @Valid
    private AdminProperties admin;

    @NotBlank(message = "{validation.field.notBlank}")
    private String contentSecurityPolicy;

    @Getter
    @Setter
    public static class JwtProperties {
        private boolean jweEnabled = true;

        @NotBlank(message = "{validation.field.notBlank}")
        private String issuer;

        @NotBlank(message = "{validation.field.notBlank}")
        private String publicKey;

        @NotBlank(message = "{validation.field.notBlank}")
        private String privateKey;

        @NotNull(message = "{validation.field.notNull}")
        private Duration accessTokenExpiration;

        @NotNull(message = "{validation.field.notNull}")
        private Duration idTokenExpiration;

        @NotNull(message = "{validation.field.notNull}")
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
        @NotBlank(message = "{validation.field.notBlank}")
        private String publicKey;

        @NotBlank(message = "{validation.field.notBlank}")
        private String privateKey;

        @NotNull(message = "{validation.field.notNull}")
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
    public static class JweProperties {
        @NotBlank(message = "{validation.field.notBlank}")
        private String publicKey;

        @NotBlank(message = "{validation.field.notBlank}")
        private String privateKey;

        @NotNull(message = "{validation.field.notNull}")
        private Duration jweExpiration;

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
        @NotBlank(message = "{validation.field.notBlank}")
        private String username;

        @NotBlank(message = "{validation.field.notBlank}")
        private String password;

        @NotBlank(message = "{validation.field.notBlank}")
        private String name;

        @NotBlank(message = "{validation.field.notBlank}")
        private String email;
    }
}