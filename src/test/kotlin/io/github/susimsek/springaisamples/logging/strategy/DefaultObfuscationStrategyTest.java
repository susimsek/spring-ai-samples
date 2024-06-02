package io.github.susimsek.springaisamples.logging.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class DefaultObfuscationStrategyTest {

    @Mock
    private LoggingProperties loggingProperties;

    @Mock
    private LoggingProperties.Obfuscate obfuscate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultObfuscationStrategy defaultObfuscationStrategy;

    @Test
    void testMaskHeaders() {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getMaskValue()).thenReturn("*****");
        when(obfuscate.getHeaders()).thenReturn(List.of("Authorization"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer secret");
        headers.add("Content-Type", "application/json");

        HttpHeaders maskedHeaders = defaultObfuscationStrategy.maskHeaders(headers);

        assertEquals("*****", maskedHeaders.getFirst("Authorization"));
        assertEquals("application/json", maskedHeaders.getFirst("Content-Type"));
    }

    @Test
    void testMaskBody() throws Exception {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getMaskValue()).thenReturn("*****");
        when(obfuscate.getJsonBodyFields()).thenReturn(List.of("$.password", "$.nested.field"));

        String body = "{\"username\":\"user\",\"password\":\"secret\",\"nested\":{\"field\":\"value\"}}";
        ObjectNode rootNode = new ObjectMapper().createObjectNode();
        rootNode.put("username", "user");
        rootNode.put("password", "secret");
        ObjectNode nestedNode = new ObjectMapper().createObjectNode();
        nestedNode.put("field", "value");
        rootNode.set("nested", nestedNode);

        when(objectMapper.readTree(body)).thenReturn(rootNode);
        when(objectMapper.writeValueAsString(rootNode)).thenReturn("{\"username\":\"user\",\"password\":\"*****\",\"nested\":{\"field\":\"*****\"}}");

        // Call the method under test
        String maskedBody = defaultObfuscationStrategy.maskBody(body);

        // Verify the results
        assertEquals("*****", rootNode.get("password").asText());
        assertEquals("*****", rootNode.get("nested").get("field").asText());
        assertEquals("{\"username\":\"user\",\"password\":\"*****\",\"nested\":{\"field\":\"*****\"}}", maskedBody);
    }

    @Test
    void testMaskBodyWithInvalidJson() throws Exception {
        String invalidBody = "{invalid json}";

        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readTree(invalidBody);

        String maskedBody = defaultObfuscationStrategy.maskBody(invalidBody);

        assertEquals(invalidBody, maskedBody);
    }

    @Test
    void testMaskBodyWithEmptyBody() {
        String emptyBody = "";

        String maskedBody = defaultObfuscationStrategy.maskBody(emptyBody);

        assertEquals(emptyBody, maskedBody);
    }

    @Test
    void testMaskBodyWithNullBody() {
        String nullBody = null;

        String maskedBody = defaultObfuscationStrategy.maskBody(nullBody);

        assertEquals(nullBody, maskedBody);
    }

    @Test
    void testMaskUriParameters() {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getMaskValue()).thenReturn("*****");
        when(obfuscate.getParameters()).thenReturn(List.of("token"));

        URI uri = URI.create("http://example.com?token=secret&name=user");
        URI maskedUri = defaultObfuscationStrategy.maskUriParameters(uri);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("token", "*****");
        queryParams.add("name", "user");

        URI expectedUri = UriComponentsBuilder.fromUri(uri).replaceQueryParams(queryParams).build().toUri();

        assertEquals(expectedUri, maskedUri);
    }

    @Test
    void testMaskJsonPaths() throws Exception {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getMaskValue()).thenReturn("*****");

        String json = "{\"username\":\"user\",\"password\":\"secret\",\"nested\":{\"field\":\"value\"}}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(json);

        Method maskJsonPaths = DefaultObfuscationStrategy.class.getDeclaredMethod("maskJsonPaths", JsonNode.class, List.class);
        maskJsonPaths.setAccessible(true);
        maskJsonPaths.invoke(defaultObfuscationStrategy, rootNode, List.of("$.nested.field"));

        assertEquals("*****", rootNode.get("nested").get("field").asText());
    }

    @Test
    void testMaskJsonPathsWithInvalidPath() throws Exception {
        String json = "{\"username\":\"user\",\"password\":\"secret\"}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(json);

        Method maskJsonPaths = DefaultObfuscationStrategy.class.getDeclaredMethod("maskJsonPaths", JsonNode.class, List.class);
        maskJsonPaths.setAccessible(true);
        maskJsonPaths.invoke(defaultObfuscationStrategy, rootNode, List.of("$.nested.field"));

        assertFalse(rootNode.has("nested"));
    }

    @Test
    void testShouldMask() throws Exception {
        Method shouldMask = DefaultObfuscationStrategy.class.getDeclaredMethod("shouldMask", List.class, String.class);
        shouldMask.setAccessible(true);

        boolean result1 = (boolean) shouldMask.invoke(defaultObfuscationStrategy, List.of("password"), "password");
        boolean result2 = (boolean) shouldMask.invoke(defaultObfuscationStrategy, List.of("password"), "username");

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void testMaskBodyWithJsonProcessingException() throws Exception {
        String body = "{\"username\":\"user\",\"password\":\"secret\",\"nested\":{\"field\":\"value\"}}";
        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readTree(body);

        String maskedBody = defaultObfuscationStrategy.maskBody(body);
        assertEquals(body, maskedBody);
    }

    @Test
    void testMaskUriParametersWithEmptyParameters() {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getParameters()).thenReturn(List.of());

        URI uri = URI.create("http://example.com?token=secret&name=user");
        URI maskedUri = defaultObfuscationStrategy.maskUriParameters(uri);

        assertEquals(uri, maskedUri);
    }

    @Test
    void testMaskJsonPathsWithEmptyPaths() throws Exception {
        String json = "{\"username\":\"user\",\"password\":\"secret\",\"nested\":{\"field\":\"value\"}}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(json);

        Method maskJsonPaths = DefaultObfuscationStrategy.class.getDeclaredMethod("maskJsonPaths", JsonNode.class, List.class);
        maskJsonPaths.setAccessible(true);
        maskJsonPaths.invoke(defaultObfuscationStrategy, rootNode, List.of());

        assertEquals("secret", rootNode.get("password").asText());
        assertEquals("value", rootNode.get("nested").get("field").asText());
    }

    @Test
    void testMaskJsonNodeRecursiveWithNullNode() throws Exception {
        Method maskJsonNodeRecursive = DefaultObfuscationStrategy.class.getDeclaredMethod("maskJsonNodeRecursive", JsonNode.class, String[].class, int.class);
        maskJsonNodeRecursive.setAccessible(true);

        maskJsonNodeRecursive.invoke(defaultObfuscationStrategy, null, new String[]{"password"}, 0);

        // Assertion to ensure no exception was thrown and nothing was masked
        assertTrue(true); // If it reaches here, it means the method handled the null node correctly
    }

    @Test
    void testMaskJsonNodeRecursiveWithIndexOutOfBounds() throws Exception {
        String json = "{\"username\":\"user\",\"password\":\"secret\"}";
        ObjectNode rootNode = (ObjectNode) new ObjectMapper().readTree(json);

        Method maskJsonNodeRecursive = DefaultObfuscationStrategy.class.getDeclaredMethod("maskJsonNodeRecursive", JsonNode.class, String[].class, int.class);
        maskJsonNodeRecursive.setAccessible(true);

        maskJsonNodeRecursive.invoke(defaultObfuscationStrategy, rootNode, new String[]{"password"}, 10);

        // Assertion to ensure no exception was thrown and nothing was masked
        assertEquals("secret", rootNode.get("password").asText()); // If the password is still "secret", it means the method handled the index out of bounds correctly
    }

}