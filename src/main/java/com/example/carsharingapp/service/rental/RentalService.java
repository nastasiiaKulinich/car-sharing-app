package com.example.carsharingapp.service.rental;

import com.example.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalDto;
import com.example.carsharingapp.dto.rental.ReturnRentalRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto save(Long userId, CreateRentalRequestDto requestDto);

    RentalDto findById(Long userId, Long rentalId, boolean isAdmin);

    RentalDto setActualReturnDate(Long rentalId, ReturnRentalRequestDto requestDto);

    List<RentalDto> findByUserIdAndActive(Long authUserId, boolean isAdmin,
                                          Long userId, Boolean isActive, Pageable pageable);

    void checkOverdueRentals();
}
