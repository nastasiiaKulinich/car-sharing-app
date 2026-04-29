package com.example.carsharingapp.mapper;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalDto;
import com.example.carsharingapp.model.Rental;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalDto toDto(Rental rental);

    Rental toModel(CreateRentalRequestDto requestDto);

    @AfterMapping
    default void setCarAndUserIds(@MappingTarget RentalDto rentalDto, Rental rental) {
        rentalDto.setCarId(rental.getCar().getId());
        rentalDto.setUserId(rental.getUser().getId());
    }
}
