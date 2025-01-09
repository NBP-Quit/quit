package com.quit.queue.application.messaging;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ReservationMessage {
    private String userId;
    private String userEmail;
    private String storeId;
    private String guestCount;
    private String reservationDate;
    private String reservationTime;

    public static ReservationMessage of(String userId, String userEmail, String storeId, String guestCount, String reservationDate, String reservationTime) {
        return ReservationMessage.builder()
                .userId(userId)
                .userEmail(userEmail)
                .storeId(storeId)
                .guestCount(guestCount)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
