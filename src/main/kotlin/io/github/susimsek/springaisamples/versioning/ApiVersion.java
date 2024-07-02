package io.github.susimsek.springaisamples.versioning;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ApiVersion {
    V1("v1"),
    V2("v2");

    private final String version;

    public static boolean isSupported(String version) {
        return Arrays.stream(ApiVersion.values())
                     .anyMatch(apiVersion -> apiVersion.version().equals(version));
    }
}