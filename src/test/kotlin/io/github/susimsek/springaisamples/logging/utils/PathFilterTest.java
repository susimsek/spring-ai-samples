package io.github.susimsek.springaisamples.logging.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.model.PathRule;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

@ExtendWith(MockitoExtension.class)
class PathFilterTest {

    @Mock
    private LoggingProperties loggingProperties;

    @InjectMocks
    private PathFilter pathFilter;

    @BeforeEach
    void setUp() {
        PathMatcher pathMatcher = new AntPathMatcher();
        pathFilter = new PathFilter(loggingProperties, pathMatcher);
    }

    @Test
    void testShouldExclude() {
        PathRule excludeRule = new PathRule();
        excludeRule.setPathPattern("/api/**");
        excludeRule.setMethods(List.of("GET"));

        LoggingProperties.Exclude exclude = new LoggingProperties.Exclude();
        exclude.setRules(List.of(excludeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setExclude(exclude);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldExclude("/api/test", "GET"));
        assertFalse(pathFilter.shouldExclude("/api/test", "POST"));
    }

    @Test
    void testShouldInclude() {
        PathRule includeRule = new PathRule();
        includeRule.setPathPattern("/api/**");
        includeRule.setMethods(List.of("POST"));

        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(List.of(includeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldInclude("/api/test", "POST"));
        assertFalse(pathFilter.shouldInclude("/api/test", "GET"));
    }

    @Test
    void testShouldIncludeWithEmptyRules() {
        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(Collections.emptyList());

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertFalse(pathFilter.shouldInclude("/api/test", "POST"));
    }

    @Test
    void testShouldExcludeWithNullRules() {
        LoggingProperties.Exclude exclude = new LoggingProperties.Exclude();
        exclude.setRules(null);

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setExclude(exclude);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertFalse(pathFilter.shouldExclude("/api/test", "GET"));
    }

    @Test
    void testShouldIncludeWithNullRules() {
        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(null);

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertFalse(pathFilter.shouldInclude("/api/test", "POST"));
    }

    @Test
    void testShouldExcludeWithEmptyMethods() {
        PathRule excludeRule = new PathRule();
        excludeRule.setPathPattern("/api/**");
        excludeRule.setMethods(Collections.emptyList());

        LoggingProperties.Exclude exclude = new LoggingProperties.Exclude();
        exclude.setRules(List.of(excludeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setExclude(exclude);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldExclude("/api/test", "GET"));
    }

    @Test
    void testShouldIncludeWithEmptyMethods() {
        PathRule includeRule = new PathRule();
        includeRule.setPathPattern("/api/**");
        includeRule.setMethods(Collections.emptyList());

        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(List.of(includeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldInclude("/api/test", "POST"));
    }

    @Test
    void testShouldExcludeWithNoMatchingPathPattern() {
        PathRule excludeRule = new PathRule();
        excludeRule.setPathPattern("/api/v1/**");
        excludeRule.setMethods(List.of("GET"));

        LoggingProperties.Exclude exclude = new LoggingProperties.Exclude();
        exclude.setRules(List.of(excludeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setExclude(exclude);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertFalse(pathFilter.shouldExclude("/api/test", "GET"));
    }

    @Test
    void testShouldIncludeWithNoMatchingPathPattern() {
        PathRule includeRule = new PathRule();
        includeRule.setPathPattern("/api/v1/**");
        includeRule.setMethods(List.of("POST"));

        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(List.of(includeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertFalse(pathFilter.shouldInclude("/api/test", "POST"));
    }

    @Test
    void testShouldExcludeWithNullMethods() {
        PathRule excludeRule = new PathRule();
        excludeRule.setPathPattern("/api/**");
        excludeRule.setMethods(null);

        LoggingProperties.Exclude exclude = new LoggingProperties.Exclude();
        exclude.setRules(List.of(excludeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setExclude(exclude);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldExclude("/api/test", "GET"));
        assertTrue(pathFilter.shouldExclude("/api/test", "POST"));
    }

    @Test
    void testShouldIncludeWithNullMethods() {
        PathRule includeRule = new PathRule();
        includeRule.setPathPattern("/api/**");
        includeRule.setMethods(null);

        LoggingProperties.Include include = new LoggingProperties.Include();
        include.setRules(List.of(includeRule));

        LoggingProperties.Http http = new LoggingProperties.Http();
        http.setInclude(include);

        when(loggingProperties.getHttp()).thenReturn(http);

        assertTrue(pathFilter.shouldInclude("/api/test", "POST"));
        assertTrue(pathFilter.shouldInclude("/api/test", "GET"));
    }
}