package io.github.susimsek.springaisamples.mapper;

import io.github.susimsek.springaisamples.dto.CityCreateDTO;
import io.github.susimsek.springaisamples.dto.CityDTO;
import io.github.susimsek.springaisamples.dto.CityUpdateDTO;
import io.github.susimsek.springaisamples.entity.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface CityMapper {
    City toEntity(CityCreateDTO cityCreateDTO);

    City toEntity(CityUpdateDTO cityUpdateDTO);

    CityDTO toDto(City city);

    void partialUpdate(CityUpdateDTO cityUpdateDTO, @MappingTarget City city);
}