package io.github.susimsek.springaisamples.logging.utils;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.model.PathRule;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

@RequiredArgsConstructor
public class PathFilter {

    private final LoggingProperties loggingProperties;
    private final PathMatcher pathMatcher;

    public boolean shouldExclude(String path, String method) {
        return applyRules(path, method, loggingProperties.getHttp().getExclude().getRules());
    }

    public boolean shouldInclude(String path, String method) {
        List<PathRule> includeRules = loggingProperties.getHttp().getInclude().getRules();
        if (CollectionUtils.isEmpty(includeRules)) {
            return false;
        }
        return applyRules(path, method, includeRules);
    }

    private boolean applyRules(String path, String method, List<PathRule> rules) {
        return Optional.ofNullable(rules).orElse(List.of()).stream()
            .anyMatch(rule -> pathMatcher.match(rule.getPathPattern(), path)
                && (CollectionUtils.isEmpty(rule.getMethods()) || rule.getMethods().contains(method)));
    }
}