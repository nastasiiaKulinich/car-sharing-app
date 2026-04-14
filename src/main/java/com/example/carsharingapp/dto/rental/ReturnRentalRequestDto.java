package com.example.carsharingapp.dto.rental;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ReturnRentalRequestDto {
    @NotNull
    @FutureOrPresent
    private LocalDate actualReturnDate;
}
