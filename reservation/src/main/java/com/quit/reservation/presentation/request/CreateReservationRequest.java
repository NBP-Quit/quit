package com.quit.reservation.presentation.request;

import com.quit.reservation.application.dto.CreateReservationDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest implements Serializable {

    @NotNull(message = "가게 ID는 필수 값입니다.")
    private UUID storeId;

    @NotNull(message = "예약 인원은 필수 값입니다.")
    private Integer guestCount;

    @NotNull(message = "예약 날짜는 필수 값입니다.")
    private LocalDate reservationDate;

    @NotNull(message = "예약 시간은 필수 값입니다.")
    private LocalTime reservationTime;

    public CreateReservationDto toDto() {
        return CreateReservationDto.of(this.storeId, this.guestCount,
                this.reservationDate, this.reservationTime);
    }
}
