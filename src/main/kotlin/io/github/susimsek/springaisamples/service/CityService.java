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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Cacheable(value = "citiesCache", key = "'page=' + #pageable.pageNumber + ',size=' + #pageable.pageSize")
    public Page<CityDTO> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(cityMapper::toDto);
    }

    @Cacheable(value = "citiesCache", key = "'all'")
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream().map(cityMapper::toDto).toList();
    }

    @Cacheable(value = "cityCache", key = "#id")
    public CityDTO getCityById(Long id) {
        return cityRepository.findById(id)
            .map(cityMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    @CachePut(value = "cityCache", key = "#result.id")
    @Caching(evict = {
        @CacheEvict(value = "citiesCache", key = "'all'"),
        @CacheEvict(value = "citiesCache", allEntries = true)
    })
    public CityDTO createCity(CityCreateDTO cityCreateDTO) {
        if (cityRepository.existsByName(cityCreateDTO.getName())) {
            throw new ResourceAlreadyExistsException("City", "name", cityCreateDTO.getName());
        }
        City city = cityMapper.toEntity(cityCreateDTO);
        city = cityRepository.save(city);
        return cityMapper.toDto(city);
    }

    @CachePut(value = "cityCache", key = "#id")
    @Caching(evict = {
        @CacheEvict(value = "citiesCache", key = "'all'"),
        @CacheEvict(value = "citiesCache", allEntries = true)
    })
    public CityDTO updateCity(Long id, CityUpdateDTO cityUpdateDTO) {
        return cityRepository.findById(id)
            .map(existingCity -> {
                if (!existingCity.getName().equals(cityUpdateDTO.getName())
                    && cityRepository.existsByName(cityUpdateDTO.getName())) {
                    throw new ResourceAlreadyExistsException("City", "name", cityUpdateDTO.getName());
                }
                cityMapper.partialUpdate(existingCity, cityUpdateDTO);
                return cityMapper.toDto(cityRepository.save(existingCity));
            })
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    @Caching(evict = {
        @CacheEvict(value = "cityCache", key = "#id"),
        @CacheEvict(value = "citiesCache", key = "'all'"),
        @CacheEvict(value = "citiesCache", allEntries = true)
    })
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        cityRepository.delete(city);
    }
}