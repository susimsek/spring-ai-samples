package io.github.susimsek.springaisamples.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Location(
    @JsonProperty String name,
    @JsonProperty String region,
    @JsonProperty String country,
    @JsonProperty double lat,
    @JsonProperty double lon,
    @JsonProperty("tz_id") String tzId,
    @JsonProperty("localtime_epoch") long localtimeEpoch,
    @JsonProperty String localtime
) {
}