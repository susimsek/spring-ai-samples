package io.github.susimsek.springaisamples.logging.utils;

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

@ExtendWith(MockitoExtension.class)
class ObfuscatorTest {

    @Mock
    private LoggingProperties loggingProperties;

    @Mock
    private LoggingProperties.Obfuscate obfuscate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private Obfuscator obfuscator;

    @Test
    void testMaskHeaders() {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getHeaders()).thenReturn(List.of("Authorization"));
        when(obfuscate.getMaskValue()).thenReturn("*****");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer secret");
        headers.add("Content-Type", "application/json");

        HttpHeaders maskedHeaders = obfuscator.maskHeaders(headers);

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

        // Test for existing field
        String maskedBody = obfuscator.maskBody(body);
        assertEquals("*****", rootNode.get("password").asText());
        assertEquals("*****", rootNode.get("nested").get("field").asText());

        // Test for non-existing field
        ObjectNode rootNode2 = new ObjectMapper().createObjectNode();
        rootNode2.put("username", "user");
        when(objectMapper.readTree("{\"username\":\"user\"}")).thenReturn(rootNode2);

        maskedBody = obfuscator.maskBody("{\"username\":\"user\"}");
        assertFalse(rootNode2.has("password"));
        assertFalse(rootNode2.has("nested"));
    }

    @Test
    void testMaskBodyWithEmptyBody() {
        // Test for empty body
        String emptyBody = "";
        String maskedBody = obfuscator.maskBody(emptyBody);
        assertEquals(emptyBody, maskedBody);

        // Test for null body
        String nullBody = null;
        maskedBody = obfuscator.maskBody(nullBody);
        assertEquals(nullBody, maskedBody);

        // Test for body with only whitespace
        String whitespaceBody = "   ";
        maskedBody = obfuscator.maskBody(whitespaceBody);
        assertEquals(whitespaceBody, maskedBody);
    }

    @Test
    void testMaskBodyCatchBlock() throws Exception {
        String invalidBody = "{invalid json}";

        doThrow(new JsonProcessingException("Invalid JSON") {}).when(objectMapper).readTree(invalidBody);

        // Test for catch block
        String maskedBody = obfuscator.maskBody(invalidBody);
        assertEquals("{invalid json}", maskedBody);
    }

    @Test
    void testMaskJsonNode() throws Exception {
        String body = "{\"username\":\"user\",\"password\":\"secret\"}";
        ObjectNode rootNode = new ObjectMapper().createObjectNode();
        rootNode.put("username", "user");
        rootNode.put("password", "secret");

        Method maskJsonNode = Obfuscator.class.getDeclaredMethod("maskJsonNode", JsonNode.class, String[].class);
        maskJsonNode.setAccessible(true);

        // Test for null currentNode
        maskJsonNode.invoke(obfuscator, null, new String[]{"password"});

        // Test for index >= pathParts.length
        maskJsonNode.invoke(obfuscator, rootNode, new String[]{});

        // Test for currentNode.has(currentPart) == false
        maskJsonNode.invoke(obfuscator, rootNode, new String[]{"nonexistent"});
        assertFalse(rootNode.has("nonexistent"));
    }

    @Test
    void testMaskUriParameters() {
        when(loggingProperties.getObfuscate()).thenReturn(obfuscate);
        when(obfuscate.getParameters()).thenReturn(List.of("token"));
        when(obfuscate.getMaskValue()).thenReturn("*****");

        URI uri = URI.create("http://example.com?token=secret&name=user");

        URI maskedUri = obfuscator.maskUriParameters(uri);
        String query = maskedUri.getQuery();

        assertTrue(query.contains("token=*****"));
        assertTrue(query.contains("name=user"));
    }

    @Test
    void testShouldMask() throws Exception {
        Method shouldMask = Obfuscator.class.getDeclaredMethod("shouldMask", List.class, String.class);
        shouldMask.setAccessible(true);

        boolean result1 = (boolean) shouldMask.invoke(obfuscator, List.of("password"), "password");
        boolean result2 = (boolean) shouldMask.invoke(obfuscator, List.of("password"), "username");

        assertTrue(result1);
        assertFalse(result2);
    }
}