package io.github.susimsek.springaisamples.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.github.susimsek.springaisamples.controller.simple.CityController;
import io.github.susimsek.springaisamples.dto.CityDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class CityModelAssembler extends RepresentationModelAssemblerSupport<CityDTO, CityDTO> {

    public CityModelAssembler() {
        super(CityController.class, CityDTO.class);
    }

    @NonNull
    @Override
    public CityDTO toModel(CityDTO city) {
        city.add(linkTo(methodOn(CityController.class)
            .getCityById(city.getId())).withSelfRel());
        city.add(linkTo(methodOn(CityController.class)
            .updateCity(city.getId(), null)).withRel("update"));
        city.add(linkTo(methodOn(CityController.class)
            .deleteCity(city.getId())).withRel("delete"));
        return city;
    }

    @NonNull
    @Override
    public CollectionModel<CityDTO> toCollectionModel(
        @NonNull Iterable<? extends CityDTO> entities) {
        CollectionModel<CityDTO> cityCollectionModel = super.toCollectionModel(entities);
        cityCollectionModel.add(linkTo(methodOn(CityController.class).getAllCities()).withSelfRel());
        return cityCollectionModel;
    }
}