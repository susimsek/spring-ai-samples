package io.github.susimsek.springaisamples.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends LocalizedException {
    private final String resourceName;
    private final String searchCriteria;
    private final transient Object searchValue;

    public ResourceNotFoundException(String resourceName, String searchCriteria, Object searchValue) {
        super("error.resource.notfound", HttpStatus.NOT_FOUND);
        this.resourceName = resourceName;
        this.searchCriteria = searchCriteria;
        this.searchValue = searchValue;
    }
}