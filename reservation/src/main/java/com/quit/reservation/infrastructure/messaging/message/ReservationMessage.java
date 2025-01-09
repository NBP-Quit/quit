package com.quit.reservation.infrastructure.messaging.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationMessage {
    private String userId;
    private String userEmail;
    private String storeId;
    private String guestCount;
    private String reservationDate;
    private String reservationTime;
}
