package io.github.susimsek.springaisamples.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String SYSTEM = "system";

    public static final String TOTAL_COUNT_HEADER_NAME = "X-Total-Count";

    public static final String SPRING_PROFILE_DEVELOPMENT = "local";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
}
