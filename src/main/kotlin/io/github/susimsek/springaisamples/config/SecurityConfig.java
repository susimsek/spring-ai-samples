package io.github.susimsek.springaisamples.config;

import static io.github.susimsek.springaisamples.security.AuthoritiesConstants.ADMIN;
import static io.github.susimsek.springaisamples.security.signature.SignatureConstants.JWS_SIGNATURE_HEADER_NAME;
import static org.springframework.security.config.Customizer.withDefaults;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.security.SecurityProblemSupport;
import io.github.susimsek.springaisamples.idempotency.IdempotencyFilter;
import io.github.susimsek.springaisamples.logging.filter.LoggingFilter;
import io.github.susimsek.springaisamples.ratelimit.RateLimitingFilter;
import io.github.susimsek.springaisamples.security.AuthoritiesConstants;
import io.github.susimsek.springaisamples.security.InMemoryTokenStore;
import io.github.susimsek.springaisamples.security.SecurityProperties;
import io.github.susimsek.springaisamples.security.TokenProvider;
import io.github.susimsek.springaisamples.security.TokenStore;
import io.github.susimsek.springaisamples.security.encryption.DecryptionFilter;
import io.github.susimsek.springaisamples.security.encryption.EncryptionFilter;
import io.github.susimsek.springaisamples.security.encryption.EncryptionUtil;
import io.github.susimsek.springaisamples.security.signature.SignatureFilter;
import io.github.susimsek.springaisamples.security.signature.SignatureVerificationFilter;
import io.github.susimsek.springaisamples.security.xss.XssFilter;
import io.github.susimsek.springaisamples.service.EncryptionService;
import io.github.susimsek.springaisamples.service.SignatureService;
import io.github.susimsek.springaisamples.trace.TraceFilter;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import io.github.susimsek.springaisamples.utils.SanitizationUtil;
import io.github.susimsek.springaisamples.validation.HeaderValidationFilter;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        MvcRequestMatcher.Builder mvc,
        RequestMatchersConfig requestMatchersConfig,
        SecurityProblemSupport problemSupport,
        DecryptionFilter decryptionFilter,
        EncryptionFilter encryptionFilter,
        SignatureVerificationFilter signatureVerificationFilter,
        SignatureFilter signatureFilter,
        XssFilter xssFilter,
        TraceFilter traceFilter,
        HeaderValidationFilter headerValidationFilter,
        IdempotencyFilter idempotencyFilter,
        RateLimitingFilter rateLimitFilter,
        LoggingFilter loggingFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(withDefaults())
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(securityProperties.getContentSecurityPolicy()))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                .referrerPolicy(
                    referrer -> referrer.policy(
                        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                ))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport))
            .authorizeHttpRequests(authz ->
                authz
                    .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
                    .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
                    .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
                    .requestMatchers(requestMatchersConfig.encryptionPaths()).permitAll()
                    .requestMatchers(requestMatchersConfig.signPath()).permitAll()
                    .requestMatchers(mvc.pattern("/.well-known/jwks.json")).permitAll()
                    .requestMatchers(mvc.pattern("/api/auth/token")).permitAll()
                    .requestMatchers(mvc.pattern("/api/locales")).permitAll()
                    .requestMatchers(mvc.pattern("/api/ai/**")).hasAuthority(ADMIN)
                    .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .jwt(withDefaults()))
            .addFilterBefore(signatureVerificationFilter, BearerTokenAuthenticationFilter.class)
            .addFilterBefore(decryptionFilter, SignatureVerificationFilter.class)
            .addFilterAfter(traceFilter, DecryptionFilter.class)
            .addFilterBefore(headerValidationFilter, TraceFilter.class)
            .addFilterBefore(loggingFilter, HeaderValidationFilter.class)
            .addFilterAfter(xssFilter, BearerTokenAuthenticationFilter.class)
            .addFilterAfter(idempotencyFilter, XssFilter.class)
            .addFilterAfter(rateLimitFilter, IdempotencyFilter.class)
            .addFilterAfter(signatureFilter, RateLimitingFilter.class)
            .addFilterAfter(encryptionFilter, SignatureFilter.class)
            .addFilterAfter(loggingFilter, EncryptionFilter.class);
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    RequestMatchersConfig requestMatchersConfig(MvcRequestMatcher.Builder mvc) {
        return new RequestMatchersConfig(mvc);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName(AuthoritiesConstants.CLAIM_NAME);
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var admin = User.withUsername(securityProperties.getAdmin().getUsername())
            .password(passwordEncoder.encode(securityProperties.getAdmin().getPassword()))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public KeyPair jwtKeyPair() {
        PublicKey publicKey = RsaKeyConverters.x509().convert(new ByteArrayInputStream(securityProperties.getJwt()
            .getFormattedPublicKey().getBytes(StandardCharsets.UTF_8)));
        PrivateKey privateKey = RsaKeyConverters.pkcs8().convert(new ByteArrayInputStream(securityProperties.getJwt()
            .getFormattedPrivateKey().getBytes(StandardCharsets.UTF_8)));
        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public KeyPair jwsKeyPair() {
        PublicKey publicKey = RsaKeyConverters.x509().convert(new ByteArrayInputStream(securityProperties.getJws()
            .getFormattedPublicKey().getBytes(StandardCharsets.UTF_8)));
        PrivateKey privateKey = RsaKeyConverters.pkcs8().convert(new ByteArrayInputStream(securityProperties.getJws()
            .getFormattedPrivateKey().getBytes(StandardCharsets.UTF_8)));
        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public KeyPair jweKeyPair() {
        PublicKey publicKey = RsaKeyConverters.x509().convert(new ByteArrayInputStream(
            securityProperties.getJwe()
            .getFormattedPublicKey().getBytes(StandardCharsets.UTF_8)));
        PrivateKey privateKey = RsaKeyConverters.pkcs8().convert(new ByteArrayInputStream(
            securityProperties.getJwe()
            .getFormattedPrivateKey().getBytes(StandardCharsets.UTF_8)));
        return new KeyPair(publicKey, privateKey);
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) jwtKeyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) jwtKeyPair.getPrivate();
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public TokenProvider tokenProvider(JwtEncoder jwtEncoder,
                                       KeyPair jwtKeyPair,
                                       KeyPair jwsKeyPair,
                                       KeyPair jweKeyPair,
                                       TokenStore tokenStore) {
        return new TokenProvider(jwtEncoder, jwtKeyPair, securityProperties,
            tokenStore, jwsKeyPair, jweKeyPair);
    }

    @Bean
    public JwtDecoder jwtDecoder(TokenProvider tokenProvider) {
        return tokenProvider::parseToken;
    }

    @Bean
    public EncryptionUtil encryptionUtil(KeyPair jweKeyPair) {
        return new EncryptionUtil(jweKeyPair);
    }

    @Bean
    public SignatureVerificationFilter signatureVerificationFilter(
        RequestMatchersConfig requestMatchersConfig,
        SignatureService signatureService,
        SecurityProblemSupport problemSupport) {
        return SignatureVerificationFilter.builder(signatureService, problemSupport)
            .order(FilterOrder.SIGNATURE_VERIFICATION.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.nonModifyingMethods()).permitAll()
            .requestMatchers(requestMatchersConfig.encryptionPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.signPath()).permitAll()
            .anyRequest().signed()
            .build();
    }

    @Bean
    public SignatureFilter signatureFilter(
        RequestMatchersConfig requestMatchersConfig,
        SignatureService signatureService,
        SecurityProblemSupport problemSupport) {
        return SignatureFilter.builder(signatureService, problemSupport)
            .order(FilterOrder.SIGNATURE.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.nonModifyingMethods()).permitAll()
            .requestMatchers(requestMatchersConfig.encryptionPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.signPath()).permitAll()
            .anyRequest().signed()
            .build();
    }

    @Bean
    public DecryptionFilter decryptionFilter(
        RequestMatchersConfig requestMatchersConfig,
        EncryptionService encryptionUtil,
        JsonUtil jsonUtil,
        SecurityProblemSupport problemSupport) {
        return DecryptionFilter.builder(encryptionUtil, problemSupport, jsonUtil)
            .order(FilterOrder.DECRYPTION.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.encryptionPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.signPath()).permitAll()
            .requestMatchers("/api/auth/token").decrypted()
            .anyRequest().permitAll()
            .build();
    }

    @Bean
    public EncryptionFilter encryptionFilter(
        RequestMatchersConfig requestMatchersConfig,
        EncryptionService encryptionUtil,
        JsonUtil jsonUtil,
        SecurityProblemSupport problemSupport) {
        return EncryptionFilter.builder(encryptionUtil, problemSupport, jsonUtil)
            .order(FilterOrder.ENCRYPTION.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.encryptionPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.signPath()).permitAll()
            .requestMatchers("/api/auth/token").encrypted()
            .anyRequest().permitAll()
            .build();
    }

    @Bean
    public XssFilter xssFilter(
        RequestMatchersConfig requestMatchersConfig,
        SanitizationUtil sanitizationUtil) {
        return XssFilter.builder(sanitizationUtil)
            .order(FilterOrder.XSS.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .anyRequest().sanitized()
            .nonSanitizedHeaders(
                HttpHeaders.CONTENT_ENCODING, HttpHeaders.CACHE_CONTROL,
                HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_LENGTH, HttpHeaders.AUTHORIZATION,
                HttpHeaders.COOKIE, HttpHeaders.HOST, HttpHeaders.USER_AGENT,
                HttpHeaders.REFERER, HttpHeaders.ACCEPT,
                JWS_SIGNATURE_HEADER_NAME,
                "sec-ch-ua",
                "sec-ch-ua-mobile",
                "sec-ch-ua-platform",
                "sec-fetch-site",
                "sec-fetch-mode",
                "sec-fetch-user",
                "sec-fetch-dest")
            .build();
    }
}