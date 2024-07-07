package io.github.susimsek.springaisamples.specification;

import io.github.susimsek.springaisamples.dto.CityFilterDTO;
import io.github.susimsek.springaisamples.entity.City;
import io.github.susimsek.springaisamples.entity.City_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CitySpecification {

    public Specification<City> createSpecification(CityFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Specification<City> spec = Specification.where(null);

            if (StringUtils.hasText(filter.name())) {
                spec = spec.and((r, q, cb) ->
                    cb.like(r.get(City_.name), "%" + filter.name() + "%"));
            }

            return spec.toPredicate(root, query, criteriaBuilder);
        };
    }
}