package com.example.carsharingapp.repository;

import com.example.carsharingapp.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = "rental")
    Optional<Payment> findBySessionId(String sessionId);

    @EntityGraph(attributePaths = "rental.user")
    List<Payment> findByRentalUserId(Long userId, Pageable pageable);
}
