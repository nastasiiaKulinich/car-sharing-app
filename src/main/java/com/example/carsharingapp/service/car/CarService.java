package com.example.carsharingapp.service.car;

import com.example.carsharingapp.dto.CarDto;
import com.example.carsharingapp.dto.CreateCarRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    void deleteById(Long id);

    CarDto updateById(Long id, CreateCarRequestDto requestDto);
}
