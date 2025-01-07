package com.quit.reservation.application.dto;

import com.quit.reservation.domain.enums.ReservationStatus;
import com.quit.reservation.domain.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GetReservationResponse implements Serializable {

    private ReservationPage reservationPage;

    public static GetReservationResponse of(Page<Reservation> reservationPage) {
        return GetReservationResponse.builder()
                .reservationPage(new ReservationPage(reservationPage))
                .build();
    }

    @Getter
    @ToString
    public static class ReservationPage extends PagedModel<ReservationPage.Reservation> {

        public ReservationPage(Page<com.quit.reservation.domain.model.Reservation> reservationPage) {
            super(
                    new PageImpl<>(
                            ReservationPage.Reservation.from(reservationPage.getContent()),
                            reservationPage.getPageable(),
                            reservationPage.getTotalElements()
                    )
            );
        }

        @Getter
        @Builder
        @AllArgsConstructor
        public static class Reservation implements Serializable {
            private UUID reservationId;
            private String customerId;
            private UUID storeId;
            private Integer guestCount;
            private LocalDate reservationDate;
            private LocalTime reservationTime;
            private ReservationStatus reservationStatus;
            private Integer reservationPrice;

            public static List<Reservation> from(List<com.quit.reservation.domain.model.Reservation> reservationList) {
                return reservationList.stream()
                        .map(ReservationPage.Reservation::from)
                        .toList();
            }

            public static Reservation from(com.quit.reservation.domain.model.Reservation reservation) {
                return ReservationPage.Reservation.builder()
                        .reservationId(reservation.getReservationId())
                        .customerId(reservation.getCustomerId())
                        .storeId(reservation.getStoreId())
                        .guestCount(reservation.getGuestCount())
                        .reservationDate(reservation.getReservationDate())
                        .reservationTime(reservation.getReservationTime())
                        .reservationStatus(reservation.getReservationStatus())
                        .reservationPrice(reservation.getReservationPrice())
                        .build();
            }

        }

    }

}
