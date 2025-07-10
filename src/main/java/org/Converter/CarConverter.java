package org.Converter;

import org.config.ModelMapperConfig;
import org.entity.Car;
import org.model.response.CarResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarConverter {

    private final ModelMapperConfig modelMapperConfig;

    @Autowired
    public CarConverter(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public CarResponse toResponse(Car carEntity) {
        return modelMapperConfig.mapper().map(carEntity, CarResponse.class);
    }
}
