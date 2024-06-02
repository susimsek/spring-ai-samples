package io.github.susimsek.springaisamples.logging.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class JsonLogFormatterTest {

    private JsonLogFormatter jsonLogFormatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jsonLogFormatter = new JsonLogFormatter(objectMapper);
    }

    @Test
    void testFormatWithBasicFields() throws Exception {
        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method("GET")
            .statusCode(200)
            .build();

        String result = jsonLogFormatter.format(httpLog);

        String expectedJson = """
            {
              "type": "request",
              "method": "GET",
              "statusCode": 200
            }
            """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(result));
    }

    @Test
    void testFormatWithUri() throws Exception {
        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method("GET")
            .uri(new URI("http://localhost"))
            .statusCode(200)
            .build();

        String result = jsonLogFormatter.format(httpLog);

        String expectedJson = """
            {
              "type": "request",
              "method": "GET",
              "statusCode": 200,
              "uri": "http://localhost",
              "host": "localhost"
            }
            """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(result));
    }

    @Test
    void testFormatWithHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method("POST")
            .uri(new URI("http://localhost"))
            .statusCode(201)
            .headers(headers)
            .build();

        String result = jsonLogFormatter.format(httpLog);

        String expectedJson = """
            {
              "type": "request",
              "method": "POST",
              "statusCode": 201,
              "uri": "http://localhost",
              "host": "localhost",
              "headers": {
                "Content-Type": [
                  "application/json"
                ]
              }
            }
            """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(result));
    }

    @Test
    void testFormatWithBody() throws Exception {
        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.RESPONSE)
            .method("POST")
            .uri(new URI("http://localhost"))
            .statusCode(201)
            .body("{\"key\":\"value\"}")
            .build();

        String result = jsonLogFormatter.format(httpLog);

        String expectedJson = """
            {
              "type": "response",
              "method": "POST",
              "statusCode": 201,
              "uri": "http://localhost",
              "host": "localhost",
              "body": {
                "key": "value"
              }
            }
            """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(result));
    }

    @Test
    void testFormatWithPlainTextBody() throws Exception {
        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.RESPONSE)
            .method("POST")
            .uri(new URI("http://localhost"))
            .statusCode(201)
            .body("plain text body")
            .build();

        String result = jsonLogFormatter.format(httpLog);

        String expectedJson = """
            {
              "type": "response",
              "method": "POST",
              "statusCode": 201,
              "uri": "http://localhost",
              "host": "localhost",
              "body": {
                "body": "plain text body"
              }
            }
            """;

        assertEquals(objectMapper.readTree(expectedJson), objectMapper.readTree(result));
    }
}