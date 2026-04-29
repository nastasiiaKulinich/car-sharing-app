package com.example.carsharingapp.repository;

import com.example.carsharingapp.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    @EntityGraph(attributePaths = {"car", "user"})
    Optional<Rental> findWithCarAndUserById(Long rentalId);

    List<Rental> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "user"})
    List<Rental> findAllByReturnDateLessThanAndActualReturnDateIsNull(LocalDate date);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findByIdAndUserId(Long rentalId, Long userId);
}
