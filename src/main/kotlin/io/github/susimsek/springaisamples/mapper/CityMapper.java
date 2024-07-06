package io.github.susimsek.springaisamples.mapper;

import io.github.susimsek.springaisamples.dto.CityCreateDTO;
import io.github.susimsek.springaisamples.dto.CityDTO;
import io.github.susimsek.springaisamples.dto.CityUpdateDTO;
import io.github.susimsek.springaisamples.entity.City;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface CityMapper {
    City toEntity(CityCreateDTO cityCreateDTO);

    City toEntity(CityUpdateDTO cityUpdateDTO);

    CityDTO toDto(City city);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget City city,
                       CityUpdateDTO cityUpdateDTO);
}