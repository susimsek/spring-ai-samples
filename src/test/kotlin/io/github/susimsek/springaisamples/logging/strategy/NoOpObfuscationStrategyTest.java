package io.github.susimsek.springaisamples.logging.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class NoOpObfuscationStrategyTest {

    private NoOpObfuscationStrategy noOpObfuscationStrategy;

    @BeforeEach
    void setUp() {
        noOpObfuscationStrategy = new NoOpObfuscationStrategy();
    }

    @Test
    void testMaskHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer secret");
        headers.add("Content-Type", "application/json");

        HttpHeaders result = noOpObfuscationStrategy.maskHeaders(headers);

        assertEquals(headers, result);
    }

    @Test
    void testMaskBody() {
        String body = "{\"username\":\"user\",\"password\":\"secret\"}";

        String result = noOpObfuscationStrategy.maskBody(body);

        assertEquals(body, result);
    }

    @Test
    void testMaskUriParameters() {
        URI uri = URI.create("http://example.com?token=secret&name=user");

        URI result = noOpObfuscationStrategy.maskUriParameters(uri);

        assertEquals(uri, result);
    }
}