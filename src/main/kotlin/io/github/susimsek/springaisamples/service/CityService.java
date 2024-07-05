package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.dto.CityCreateDTO;
import io.github.susimsek.springaisamples.dto.CityDTO;
import io.github.susimsek.springaisamples.dto.CityUpdateDTO;
import io.github.susimsek.springaisamples.entity.City;
import io.github.susimsek.springaisamples.exception.ResourceAlreadyExistsException;
import io.github.susimsek.springaisamples.exception.ResourceNotFoundException;
import io.github.susimsek.springaisamples.mapper.CityMapper;
import io.github.susimsek.springaisamples.repository.CityRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream()
            .map(cityMapper::toDto)
            .toList();
    }

    public Optional<CityDTO> getCityById(Long id) {
        return cityRepository.findById(id)
            .map(cityMapper::toDto);
    }

    public CityDTO createCity(CityCreateDTO cityCreateDTO) {
        if (cityRepository.existsByName(cityCreateDTO.getName())) {
            throw new ResourceAlreadyExistsException("City", "name", cityCreateDTO.getName());
        }
        City city = cityMapper.toEntity(cityCreateDTO);
        return cityMapper.toDto(cityRepository.save(city));
    }

    public CityDTO updateCity(Long id, CityUpdateDTO cityUpdateDTO) {
        return cityRepository.findById(id)
            .map(existingCity -> {
                cityMapper.partialUpdate(cityUpdateDTO, existingCity);
                return cityMapper.toDto(cityRepository.save(existingCity));
            })
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        cityRepository.delete(city);
    }
}