package io.github.susimsek.springaisamples.security.signature;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SignatureConstants {

    public static final String CLAIM_NAME = "data";

    public static final String JWS_SIGNATURE_HEADER_NAME = "X-JWS-Signature";
}
