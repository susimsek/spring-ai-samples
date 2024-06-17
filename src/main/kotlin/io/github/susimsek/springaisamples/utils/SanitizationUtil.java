package io.github.susimsek.springaisamples.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SanitizationUtil {

    private final ObjectMapper objectMapper;

    public String sanitizeInput(String input) {
        return StringEscapeUtils.escapeHtml4(input);
    }

    public JsonNode sanitizeJsonNode(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    ((ObjectNode) jsonNode).put(entry.getKey(), sanitizeInput(value.textValue()));
                } else if (value.isContainerNode()) {
                    sanitizeJsonNode(value);
                }
            });
        } else if (jsonNode.isArray()) {
            jsonNode.forEach(this::sanitizeJsonNode);
        }
        return jsonNode;
    }

    public String sanitizeJsonString(String jsonString) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            jsonNode = sanitizeJsonNode(jsonNode);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            return jsonString; // If it's not a valid JSON, return the original string
        }
    }
}