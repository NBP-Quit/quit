package com.quit.queue.application.messaging;

import com.quit.queue.application.dto.ReservationDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ReservationMessage {
    private String userId;
    private UUID storeId;
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;

    public static ReservationMessage of(ReservationDto dto, String userId, UUID storeId) {
        return ReservationMessage.builder()
                .userId(userId)
                .storeId(storeId)
                .guestCount(dto.getGuestCount())
                .reservationDate(dto.getReservationDate())
                .reservationTime(dto.getReservationTime())
                .build();
    }
}
