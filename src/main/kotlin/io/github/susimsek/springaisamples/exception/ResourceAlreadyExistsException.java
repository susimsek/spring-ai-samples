package io.github.susimsek.springaisamples.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceAlreadyExistsException extends LocalizedException {
    private final String resourceName;
    private final String searchCriteria;
    private final transient Object searchValue;

    public ResourceAlreadyExistsException(String resourceName, String searchCriteria, Object searchValue) {
        super("error.resource.alreadyexists", HttpStatus.CONFLICT);
        this.resourceName = resourceName;
        this.searchCriteria = searchCriteria;
        this.searchValue = searchValue;
    }
}