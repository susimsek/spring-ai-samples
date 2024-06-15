package io.github.susimsek.springaisamples.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for Spring Security authorities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatureConstants {

    public static final String CLAIM_NAME = "data";

    public static final String JWS_SIGNATURE_HEADER_NAME = "X-JWS-Signature";
}
