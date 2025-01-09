package com.quit.reservation.domain.service;

import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.presentation.exception.CustomException;
import com.quit.reservation.presentation.exception.error.ErrorType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ReservationValidationService {

    public void validateGuestCount(int guestCount) {
        if (guestCount <= 0) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "예약 인원은 최소 1명 이상이어야 합니다.");
        }
    }

    public void validateReservationDate(LocalDate reservationDate) {
        LocalDate today = LocalDate.now();
        if (reservationDate.isBefore(today) || reservationDate.isEqual(today)) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "잘못된 날짜 값입니다.");
        }
    }

    public void validateReservationTime(LocalTime reservationTime) {
        if (reservationTime.isBefore(LocalTime.of(0, 0))
                || reservationTime.isAfter(LocalTime.of(23, 59))) {
            throw new CustomException(ErrorType.COMMON_INVALID_PARAMETER, "잘못된 시간 값입니다.");
        }
    }

    public void validateChangeReservationStatus(ReservationStatus requestStatus, ReservationStatus reservationStatus) {
        if (requestStatus.equals(ReservationStatus.CANCELED) || reservationStatus.equals(ReservationStatus.CANCELED)
                || reservationStatus.equals(ReservationStatus.COMPLETED)) {
            throw new CustomException(ErrorType.FAILED_CHANGE_RESERVATION_STATUS);
        }
    }

    public void validateCancelReservationStatus(ReservationStatus status) {
        if (status.equals(ReservationStatus.CANCELED) || status.equals(ReservationStatus.COMPLETED)) {
            throw new CustomException(ErrorType.FAILED_CHANGE_RESERVATION_STATUS);
        }
    }

    public void validateReservationStatusForDelete(ReservationStatus status) {
        if (!status.equals(ReservationStatus.CANCELED)) {
            throw new CustomException(ErrorType.FAILED_CHANGE_RESERVATION_STATUS);
        }
    }
}
