package io.github.susimsek.springaisamples.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheName {
    public static final String CITIES_CACHE = "citiesCache";
    public static final String CITY_CACHE = "cityCache";
    public static final String DEFAULT_UPDATE_TIMESTAMPS_REGION = "default-update-timestamps-region";
    public static final String DEFAULT_QUERY_RESULTS_REGION = "default-query-results-region";
}