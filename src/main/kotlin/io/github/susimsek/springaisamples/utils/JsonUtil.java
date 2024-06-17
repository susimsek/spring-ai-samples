package io.github.susimsek.springaisamples.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String convertToJsonString(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(value);
            return objectMapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            return value;
        }
    }

    public String convertObjectToString(Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof String stringValue) {
                JsonNode jsonNode = objectMapper.readTree(stringValue);
                return objectMapper.writeValueAsString(jsonNode);
            }
            return objectMapper.writeValueAsString(value);
        } catch (IOException e) {
            return value.toString();
        }
    }
}