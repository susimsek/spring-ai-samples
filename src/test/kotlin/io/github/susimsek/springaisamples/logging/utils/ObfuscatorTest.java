package io.github.susimsek.springaisamples.logging.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.strategy.ObfuscationStrategy;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class ObfuscatorTest {

    @Mock
    private ObfuscationStrategy obfuscationStrategy;

    @InjectMocks
    private Obfuscator obfuscator;

    @Test
    void testMaskHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer secret");
        headers.add("Content-Type", "application/json");

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.add("Authorization", "*****");
        expectedHeaders.add("Content-Type", "application/json");

        when(obfuscationStrategy.maskHeaders(headers)).thenReturn(expectedHeaders);

        HttpHeaders maskedHeaders = obfuscator.maskHeaders(headers);

        assertEquals("*****", maskedHeaders.getFirst("Authorization"));
        assertEquals("application/json", maskedHeaders.getFirst("Content-Type"));
    }

    @Test
    void testMaskBody() {
        String body = "{\"username\":\"user\",\"password\":\"secret\"}";
        String expectedBody = "{\"username\":\"user\",\"password\":\"*****\"}";

        when(obfuscationStrategy.maskBody(body)).thenReturn(expectedBody);

        String maskedBody = obfuscator.maskBody(body);

        assertEquals(expectedBody, maskedBody);
    }

    @Test
    void testMaskUriParameters() {
        URI uri = URI.create("http://example.com?token=secret&name=user");
        URI expectedUri = URI.create("http://example.com?token=*****&name=user");

        when(obfuscationStrategy.maskUriParameters(uri)).thenReturn(expectedUri);

        URI maskedUri = obfuscator.maskUriParameters(uri);

        assertEquals(expectedUri, maskedUri);
    }
}