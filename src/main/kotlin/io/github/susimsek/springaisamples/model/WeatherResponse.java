package io.github.susimsek.springaisamples.model;

public record WeatherResponse(
    Location location,
    Current current
) {
}