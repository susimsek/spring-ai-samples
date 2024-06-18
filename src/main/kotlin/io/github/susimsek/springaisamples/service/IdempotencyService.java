package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.idempotency.CachedResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final Map<String, CachedResponse> idempotencyKeys = new ConcurrentHashMap<>();

    public boolean containsKey(String key) {
        return idempotencyKeys.containsKey(key);
    }

    public CachedResponse getResponse(String key) {
        return idempotencyKeys.get(key);
    }

    public void saveResponse(String key, CachedResponse response) {
        idempotencyKeys.put(key, response);
    }
}