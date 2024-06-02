package io.github.susimsek.springaisamples.logging.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class DefaultObfuscationStrategy implements ObfuscationStrategy {

    private final LoggingProperties loggingProperties;
    private final ObjectMapper objectMapper;

    @Override
    public HttpHeaders maskHeaders(HttpHeaders headers) {
        HttpHeaders maskedHeaders = new HttpHeaders();
        headers.forEach((key, value) -> maskedHeaders.addAll(key,
            shouldMask(loggingProperties.getObfuscate().getHeaders(), key)
                ? List.of(loggingProperties.getObfuscate().getMaskValue())
                : value));
        return maskedHeaders;
    }

    @Override
    public String maskBody(String body) {
        if (!StringUtils.hasText(body)) {
            return body;
        }
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            maskJsonPaths(rootNode, loggingProperties.getObfuscate().getJsonBodyFields());
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return body;
        }
    }

    @Override
    public URI maskUriParameters(URI uri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
        MultiValueMap<String, String> queryParams = builder.build().getQueryParams();
        MultiValueMap<String, String> maskedParams = new LinkedMultiValueMap<>();

        queryParams.forEach((key, value) -> maskedParams.addAll(key,
            shouldMask(loggingProperties.getObfuscate().getParameters(), key)
                ? List.of(loggingProperties.getObfuscate().getMaskValue())
                : value));

        builder.replaceQueryParams(maskedParams);
        return builder.build().toUri();
    }

    private void maskJsonPaths(JsonNode rootNode, List<String> jsonPaths) {
        jsonPaths.forEach(jsonPath -> {
            String[] pathParts = jsonPath.replace("$.", "").split("\\.");
            maskJsonNodeRecursive(rootNode, pathParts, 0);
        });
    }

    private void maskJsonNodeRecursive(JsonNode currentNode, String[] pathParts, int index) {
        if (currentNode == null || index >= pathParts.length) {
            return;
        }

        String currentPart = pathParts[index];
        if (currentNode.has(currentPart)) {
            if (index == pathParts.length - 1) {
                ((ObjectNode) currentNode).set(
                    currentPart,
                    JsonNodeFactory.instance.textNode(loggingProperties.getObfuscate().getMaskValue()));
            } else {
                maskJsonNodeRecursive(currentNode.get(currentPart), pathParts, index + 1);
            }
        }
    }

    private boolean shouldMask(List<String> list, String key) {
        return list.contains(key);
    }
}