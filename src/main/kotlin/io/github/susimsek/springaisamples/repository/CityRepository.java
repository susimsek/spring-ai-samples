package io.github.susimsek.springaisamples.repository;

import io.github.susimsek.springaisamples.entity.City;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    String CITIES_BY_NAME_CACHE = "citiesByNameCache";

    @Cacheable(cacheNames = CITIES_BY_NAME_CACHE)
    boolean existsByName(String name);
}