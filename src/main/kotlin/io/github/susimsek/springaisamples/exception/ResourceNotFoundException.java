package io.github.susimsek.springaisamples.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends LocalizedException {
    private final String resourceName;
    private final String searchCriteria;
    private final transient Object searchValue;

    public ResourceNotFoundException(String resourceName, String searchCriteria, Object searchValue) {
        super("error.resource.notfound", HttpStatus.NOT_FOUND,
            createNamedArgs(resourceName, searchCriteria, searchValue));
        this.resourceName = resourceName;
        this.searchCriteria = searchCriteria;
        this.searchValue = searchValue;
    }

    private static Map<String, String> createNamedArgs(String resourceName,
                                                       String searchCriteria, Object searchValue) {
        Map<String, String> namedArgs = new HashMap<>();
        namedArgs.put("resource", resourceName);
        namedArgs.put("criteria", searchCriteria);
        namedArgs.put("value", searchValue.toString());
        return namedArgs;
    }
}