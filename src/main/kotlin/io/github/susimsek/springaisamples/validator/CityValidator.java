package io.github.susimsek.springaisamples.validator;

import io.github.susimsek.springaisamples.exception.ResourceAlreadyExistsException;
import io.github.susimsek.springaisamples.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityValidator {

    private final CityRepository cityRepository;

    public void validateCityNameDoesNotExist(String name) {
        if (cityRepository.existsByName(name)) {
            throw new ResourceAlreadyExistsException("City", "name", name);
        }
    }

    public void validateUpdatedCityName(String newName, String existingName) {
        if (!existingName.equals(newName) && cityRepository.existsByName(newName)) {
            throw new ResourceAlreadyExistsException("City", "name", newName);
        }
    }
}