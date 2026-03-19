package com.example.carsharingapp.mapper;

import com.example.carsharingapp.config.MapperConfig;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserResponseWithRolesDto;
import com.example.carsharingapp.model.User;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(target = "roles", ignore = true)
    UserResponseWithRolesDto toDtoWithRoles(User user);

    @AfterMapping
    default void setRoles(@MappingTarget UserResponseWithRolesDto userDto, User user) {
        userDto.setRoles(user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet()));
    }
}
