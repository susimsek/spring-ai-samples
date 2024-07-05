package io.github.susimsek.springaisamples.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record Current(
    @JsonProperty("last_updated_epoch") long lastUpdatedEpoch,
    @JsonProperty LocalDateTime lastUpdated,
    @JsonProperty("temp_c") double tempC,
    @JsonProperty("temp_f") double tempF,
    @JsonProperty("is_day") int isDay,
    @JsonProperty Condition condition,
    @JsonProperty("wind_mph") double windMph,
    @JsonProperty("wind_kph") double windKph,
    @JsonProperty("wind_degree") int windDegree,
    @JsonProperty("wind_dir") String windDir,
    @JsonProperty("pressure_mb") double pressureMb,
    @JsonProperty("pressure_in") double pressureIn,
    @JsonProperty("precip_mm") double precipMm,
    @JsonProperty("precip_in") double precipIn,
    @JsonProperty int humidity,
    @JsonProperty int cloud,
    @JsonProperty("feelslike_c") double feelslikeC,
    @JsonProperty("feelslike_f") double feelslikeF,
    @JsonProperty("windchill_c") double windchillC,
    @JsonProperty("windchill_f") double windchillF,
    @JsonProperty("heatindex_c") double heatindexC,
    @JsonProperty("heatindex_f") double heatindexF,
    @JsonProperty("dewpoint_c") double dewpointC,
    @JsonProperty("dewpoint_f") double dewpointF,
    @JsonProperty("vis_km") double visKm,
    @JsonProperty("vis_miles") double visMiles,
    @JsonProperty double uv,
    @JsonProperty("gust_mph") double gustMph,
    @JsonProperty("gust_kph") double gustKph
) {
}