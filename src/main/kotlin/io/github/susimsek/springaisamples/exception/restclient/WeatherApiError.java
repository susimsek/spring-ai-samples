package io.github.susimsek.springaisamples.exception.restclient;

public record WeatherApiError(int code, String message) {}