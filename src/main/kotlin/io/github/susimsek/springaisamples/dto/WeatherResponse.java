package io.github.susimsek.springaisamples.dto;

public record WeatherResponse(
    Location location,
    Current current
) {
}