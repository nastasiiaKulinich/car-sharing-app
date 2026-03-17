package com.example.carsharingapp.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private String carType;
    private int inventory;
    private BigDecimal dailyFee;
}
