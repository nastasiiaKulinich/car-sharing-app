package com.example.carsharingapp.service.notification.template;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NotificationTemplates {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getTemplate(NotificationType type, Object... params) {
        return switch (type) {
            case NEW_RENTAL -> formatNewRental(
                    (Long) params[0],
                    (String) params[1],
                    (String) params[2],
                    (Long) params[3],
                    (LocalDate) params[4],
                    (LocalDate) params[5]
            );

            case OVERDUE_RENTAL -> formatOverdueRental(
                    (Long) params[0],
                    (String) params[1],
                    (String) params[2],
                    (String) params[3],
                    (String) params[4],
                    (LocalDate) params[5],
                    (Long) params[6]
            );
        };
    }

    private static String formatNewRental(Long carId, String carBrand, String carModel,
                                          Long userId, LocalDate rentalDate,
                                          LocalDate returnDate) {
        return """
                🚗 New Rental Created 🚗
                CarId: %d
                CarBrand: %s
                CarModel: %s
                UserId: %s
                Rental Date: %s
                Return Date: %s
                """.formatted(carId, carBrand, carModel, userId,
                rentalDate.format(DATE_FORMATTER),
                returnDate.format(DATE_FORMATTER));
    }

    private static String formatOverdueRental(Long carId, String carBrand, String carModel,
                                              String userName, String userEmail,
                                              LocalDate dateForReturn, Long overduePeriod) {
        return """
                ⚠️ Overdue Rental ⚠️
                СarID: %d
                CarBrand: %s
                CarModel: %s
                User Name: %s
                User Email: %s
                Date for return: %s
                Overdue: %d days
                """.formatted(carId, carBrand, carModel, userName, userEmail,
                dateForReturn.format(DATE_FORMATTER), overduePeriod);
    }
}
