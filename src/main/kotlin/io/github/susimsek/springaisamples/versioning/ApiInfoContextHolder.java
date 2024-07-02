package io.github.susimsek.springaisamples.versioning;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiInfoContextHolder {

    private static final ThreadLocal<ApiInfo> apiInfoContext = new ThreadLocal<>();

    public static void setApiInfo(ApiInfo apiInfo) {
        apiInfoContext.set(apiInfo);
    }

    public static ApiInfo getApiInfo() {
        return apiInfoContext.get();
    }

    public static void clear() {
        apiInfoContext.remove();
    }
}