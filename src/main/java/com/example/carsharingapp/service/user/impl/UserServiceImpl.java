package com.example.carsharingapp.service.user.impl;

import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.dto.user.UserResponseWithRolesDto;
import com.example.carsharingapp.dto.user.UserUpdateProfileRequestDto;
import com.example.carsharingapp.dto.user.UserUpdateRolesRequestDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.exception.RegistrationException;
import com.example.carsharingapp.mapper.UserMapper;
import com.example.carsharingapp.model.Role;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.RoleRepository;
import com.example.carsharingapp.repository.UserRepository;
import com.example.carsharingapp.service.user.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(String.format("User with this email: %s already exists",
                    requestDto.getEmail()));
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findRoleByName(Role.RoleName.CUSTOMER));
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getProfile(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfile(Long userId, UserUpdateProfileRequestDto requestDto) {
        User user = findUserById(userId);
        user.setEmail(requestDto.getEmail());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseWithRolesDto updateUserRoles(Long userId,
                                                    UserUpdateRolesRequestDto requestDto) {
        User user = findUserById(userId);
        Set<Role> setRoles = requestDto.getRoles().stream()
                .map(roleName -> Optional.ofNullable(roleRepository.findRoleByName(roleName))
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                                + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(setRoles);
        userRepository.save(user);
        return userMapper.toDtoWithRoles(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id = "
                        + userId));
    }
}
