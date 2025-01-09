package com.quit.payment.infrastructure.client;

import com.quit.payment.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationGatewayImpl implements ReservationGateway {

    private final ReservationClient reservationClient;

    @Override
    public ApiResponse<UUID> getReservation(UUID reservationId) {
        return reservationClient.getReservation(reservationId);
    }

}
