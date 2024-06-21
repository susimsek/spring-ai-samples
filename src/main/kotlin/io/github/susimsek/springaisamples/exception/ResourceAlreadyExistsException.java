package io.github.susimsek.springaisamples.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends LocalizedException {
    public ResourceAlreadyExistsException(String resourceName, String searchCriteria, Object searchValue) {
        super("error.resource.alreadyexists", HttpStatus.CONFLICT, resourceName, searchCriteria, searchValue);
    }
}