package com.quit.payment.infrastructure.client;

import com.quit.payment.application.dto.PaymentDto;
import com.quit.payment.configuration.PaymentFeignConfig;
import com.quit.payment.infrastructure.dto.ConfirmPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PaymentClient", url = "${payment.base-url}", configuration = PaymentFeignConfig.class)
public interface PaymentClient {

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    ConfirmPaymentResponse confirmPayment(@RequestBody PaymentDto request);
}
