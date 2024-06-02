package io.github.susimsek.springaisamples.logging.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Slf4j
public class Obfuscator {

    private final LoggingProperties loggingProperties;
    private final ObjectMapper objectMapper;

    public HttpHeaders maskHeaders(HttpHeaders headers) {
        HttpHeaders maskedHeaders = new HttpHeaders();
        headers.forEach((key, value) -> maskedHeaders.addAll(key,
            shouldMask(loggingProperties.getObfuscate().getHeaders(), key)
                ? List.of(loggingProperties.getObfuscate().getMaskValue())
                : value));
        return maskedHeaders;
    }

    public String maskBody(String body) {
        if (!StringUtils.hasText(body)) {
            return body;
        }
        try {
            JsonNode rootNode = objectMapper.readTree(body);
            maskJsonPaths(rootNode, loggingProperties.getObfuscate().getJsonBodyFields());
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            log.error("Error while masking body: ", e);
            return body;
        }
    }

    private void maskJsonPaths(JsonNode rootNode, List<String> jsonPaths) {
        jsonPaths.stream()
            .map(jsonPath -> jsonPath.replace("$.", "").split("\\."))
            .forEach(pathParts -> maskJsonNode(rootNode, pathParts));
    }

    private void maskJsonNode(JsonNode currentNode, String[] pathParts) {
        for (int i = 0; i < pathParts.length && currentNode != null; i++) {
            String part = pathParts[i];
            if (currentNode.has(part)) {
                if (i == pathParts.length - 1) {
                    ((ObjectNode) currentNode).set(
                        part,
                        JsonNodeFactory.instance.textNode(loggingProperties.getObfuscate().getMaskValue()));
                } else {
                    currentNode = currentNode.get(part);
                }
            }
        }
    }

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

    private boolean shouldMask(List<String> list, String key) {
        return list.contains(key);
    }
}