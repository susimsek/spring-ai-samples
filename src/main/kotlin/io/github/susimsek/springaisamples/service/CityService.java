package io.github.susimsek.springaisamples.service;

import io.github.susimsek.springaisamples.constant.CacheName;
import io.github.susimsek.springaisamples.dto.CityCreateDTO;
import io.github.susimsek.springaisamples.dto.CityDTO;
import io.github.susimsek.springaisamples.dto.CityFilterDTO;
import io.github.susimsek.springaisamples.dto.CityUpdateDTO;
import io.github.susimsek.springaisamples.entity.City;
import io.github.susimsek.springaisamples.exception.ResourceNotFoundException;
import io.github.susimsek.springaisamples.mapper.CityMapper;
import io.github.susimsek.springaisamples.repository.CityRepository;
import io.github.susimsek.springaisamples.specification.CitySpecification;
import io.github.susimsek.springaisamples.validator.CityValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private final CityValidator cityValidator;

    @Cacheable(value = CacheName.CITIES_CACHE, key = "'page=' + #pageable.pageNumber "
        + "+ ',size=' + #pageable.pageSize")
    public Page<CityDTO> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable).map(cityMapper::toDto);
    }

    @Cacheable(value = CacheName.CITIES_CACHE, key = "'all'")
    public List<CityDTO> getAllCities() {
        return cityRepository.findAll().stream().map(cityMapper::toDto).toList();
    }

    @Cacheable(value = CacheName.CITIES_CACHE, key = "'filter=' "
        + "+ #filter.name() + ',page=' + #pageable.pageNumber + ',size=' + #pageable.pageSize")
    public Page<CityDTO> getAllCities(Pageable pageable, CityFilterDTO filter) {
        Specification<City> spec = CitySpecification.createSpecification(filter);
        return cityRepository.findAll(spec, pageable).map(cityMapper::toDto);
    }

    @Cacheable(value = CacheName.CITIES_CACHE, key = "'filter=' + #filter.name()")
    public List<CityDTO> getAllCities(CityFilterDTO filter) {
        Specification<City> spec = CitySpecification.createSpecification(filter);
        return cityRepository.findAll(spec).stream().map(cityMapper::toDto).toList();
    }


    @Cacheable(value = CacheName.CITY_CACHE, key = "#id")
    public CityDTO getCityById(Long id) {
        City city = findCityById(id);
        return cityMapper.toDto(city);
    }

    @CachePut(value = CacheName.CITY_CACHE, key = "#result.id")
    @Caching(evict = {
        @CacheEvict(value = CacheName.CITIES_CACHE, key = "'all'"),
        @CacheEvict(value = CacheName.CITIES_CACHE, allEntries = true)
    })
    public CityDTO createCity(CityCreateDTO cityCreateDTO) {
        cityValidator.validateCityNameDoesNotExist(cityCreateDTO.name());
        City city = cityMapper.toEntity(cityCreateDTO);
        city = cityRepository.save(city);
        return cityMapper.toDto(city);
    }

    @CachePut(value = CacheName.CITY_CACHE, key = "#id")
    @Caching(evict = {
        @CacheEvict(value = CacheName.CITIES_CACHE, key = "'all'"),
        @CacheEvict(value = CacheName.CITIES_CACHE, allEntries = true)
    })
    public CityDTO updateCity(Long id, CityUpdateDTO cityUpdateDTO) {
        City existingCity = findCityById(id);
        cityValidator.validateUpdatedCityName(cityUpdateDTO.name(), existingCity.getName());
        cityMapper.partialUpdate(existingCity, cityUpdateDTO);
        City updatedCity = cityRepository.save(existingCity);
        return cityMapper.toDto(updatedCity);
    }

    @Caching(evict = {
        @CacheEvict(value = CacheName.CITY_CACHE, key = "#id"),
        @CacheEvict(value = CacheName.CITIES_CACHE, key = "'all'"),
        @CacheEvict(value = CacheName.CITIES_CACHE, allEntries = true)
    })
    public void deleteCity(Long id) {
        City city = findCityById(id);
        cityRepository.delete(city);
    }

    private City findCityById(Long id) {
        return cityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }
}