package com.example.carsharingapp.service.car.impl;

import com.example.carsharingapp.dto.car.CarDto;
import com.example.carsharingapp.dto.car.CreateCarRequestDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.mapper.CarMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.repository.CarRepository;
import com.example.carsharingapp.service.car.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto findById(Long id) {
        Car car = findCarById(id);
        return carMapper.toDto(car);
    }

    @Override
    public void deleteById(Long id) {
        Car car = findCarById(id);
        car.setDeleted(true);
        carRepository.save(car);
    }

    @Override
    public CarDto updateById(Long id, CreateCarRequestDto requestDto) {
        findCarById(id);
        Car car = carMapper.toModel(requestDto);
        car.setId(id);
        return carMapper.toDto(carRepository.save(car));
    }

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id = " + id));
    }
}
