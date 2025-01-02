package com.quit.reservation.domain.enums;

public enum ReservationStatus {

    PENDING,       // 예약 대기
    ACCEPTED,      // 예약 접수
    CONFIRMED,     // 예약 확정(완료)
    CANCELED,      // 예약 취소
    COMPLETED;     // 방문 완료
}
