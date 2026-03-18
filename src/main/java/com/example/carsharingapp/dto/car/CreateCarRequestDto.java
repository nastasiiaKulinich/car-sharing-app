package com.example.carsharingapp.dto.car;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotBlank
    private String carType;
    @Min(0)
    private int inventory;
    @NotNull
    @Min(0)
    private BigDecimal dailyFee;
}
