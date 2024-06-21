package io.github.susimsek.springaisamples.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(String resourceName, String searchCriteria, Object searchValue) {
        super("error.resource.notfound", HttpStatus.NOT_FOUND, resourceName, searchCriteria, searchValue);
    }

}