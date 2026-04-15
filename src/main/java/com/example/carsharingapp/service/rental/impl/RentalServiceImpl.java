package com.example.carsharingapp.service.rental.impl;

import com.example.carsharingapp.dto.rental.CreateRentalRequestDto;
import com.example.carsharingapp.dto.rental.RentalDto;
import com.example.carsharingapp.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingapp.exception.RentalException;
import com.example.carsharingapp.mapper.RentalMapper;
import com.example.carsharingapp.model.Car;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.model.User;
import com.example.carsharingapp.repository.CarRepository;
import com.example.carsharingapp.repository.RentalRepository;
import com.example.carsharingapp.repository.UserRepository;
import com.example.carsharingapp.service.notification.NotificationService;
import com.example.carsharingapp.service.notification.template.NotificationTemplates;
import com.example.carsharingapp.service.notification.template.NotificationType;
import com.example.carsharingapp.service.rental.RentalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int DECREASE_BY_ONE = 1;
    private static final int INCREASE_BY_ONE = 1;

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    @Override
    public RentalDto save(Long userId, CreateRentalRequestDto requestDto) {
        User user = findUserById(userId);
        Car car = findCarById(requestDto.getCarId());
        if (car.getInventory() < 1) {
            throw new RentalException("There are no available cars");
        }
        car.setInventory(car.getInventory() - DECREASE_BY_ONE);
        Rental rental = rentalMapper.toModel(requestDto);
        rental.setUser(user);
        rental.setCar(car);
        rental.setRentalDate(requestDto.getRentalDate());
        rental.setReturnDate(requestDto.getReturnDate());
        RentalDto rentalDto = rentalMapper.toDto(rentalRepository.save(rental));
        notificationService.sendMessageAdmin(NotificationTemplates.getTemplate(
                NotificationType.NEW_RENTAL,
                car.getId(),
                car.getBrand(),
                car.getModel(),
                user.getId(),
                rentalDto.getRentalDate(),
                rentalDto.getReturnDate()));
        return rentalDto;
    }

    @Override
    public RentalDto findById(Long userId, Long rentalId, boolean isAdmin) {
        Rental rental = findRentalById(rentalId);
        if (!isAdmin && !rental.getUser().getId().equals(userId)) {
            throw new RentalException("You don`t have permission to see this rental");
        }
        return rentalMapper.toDto(rental);
    }

    @Transactional
    @Override
    public RentalDto setActualReturnDate(Long rentalId, ReturnRentalRequestDto requestDto) {
        Rental rental = findRentalById(rentalId);
        if (!rental.isActive()) {
            throw new RentalException("The rental is already closed!");
        }
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + INCREASE_BY_ONE);
        carRepository.save(car);
        rental.setActualReturnDate(requestDto.getActualReturnDate());
        rental.setActive(false);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    @Override
    public List<RentalDto> findByUserIdAndActive(Long authUserId, boolean isAdmin,
                                                 Long userId, Boolean isActive, Pageable pageable) {
        if (!isAdmin && userId != null && !userId.equals(authUserId)) {
            throw new RentalException("You cannot access other users' rentals");
        }
        List<Rental> rentals;
        if (isAdmin) {
            if (userId != null) {
                rentals = rentalRepository.findByUserId(userId, pageable);
            } else {
                rentals = rentalRepository.findAll(pageable).getContent();
            }
        } else {
            rentals = rentalRepository.findByUserId(authUserId, pageable);
        }
        if (isActive != null) {
            rentals = rentals.stream()
                    .filter(r -> r.isActive() == isActive)
                    .toList();
        }
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *", zone = "Europe/Kiev")
    public void checkOverdueRentals() {
        LocalDate today = LocalDate.now();
        List<Rental> overdueRentals = rentalRepository
                .findAllByReturnDateLessThanAndActualReturnDateIsNull(today);
        if (overdueRentals.isEmpty()) {
            notificationService.sendMessageAdmin("✅ No rentals overdue today!");
            return;
        }
        for (Rental rental : overdueRentals) {
            notificationService.sendMessageAdmin(NotificationTemplates.getTemplate(
                    NotificationType.OVERDUE_RENTAL,
                    rental.getCar().getId(),
                    rental.getCar().getBrand(),
                    rental.getCar().getModel(),
                    rental.getUser().getFirstName() + " " + rental.getUser().getLastName(),
                    rental.getUser().getEmail(),
                    rental.getReturnDate(),
                    ChronoUnit.DAYS.between(rental.getReturnDate(), today)));
        }
    }

    private Rental findRentalById(Long rentalId) {
        return rentalRepository.findWithCarAndUserById(rentalId).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental by id = " + rentalId));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id = " + userId));
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id = " + carId));
    }
}
