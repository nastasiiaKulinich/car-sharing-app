package com.example.carsharingapp.service.user;

import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserResponseWithRolesDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileRequestDto;
import com.example.carsharingapp.dto.user.UserUpdateRolesRequestDto;
import com.example.carsharingapp.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserResponseDto getProfile(User user);

    UserResponseDto updateProfile(
            Long userId, UserUpdateProfileRequestDto requestDto);

    UserResponseWithRolesDto updateUserRoles(
            Long userId, UserUpdateRolesRequestDto requestDto);
}
