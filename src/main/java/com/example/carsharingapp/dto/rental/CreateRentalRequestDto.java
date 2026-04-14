package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull
    @FutureOrPresent
    private LocalDate rentalDate;
    @NotNull
    @FutureOrPresent
    private LocalDate returnDate;
    @NotNull
    @Min(0)
    private Long carId;
}
