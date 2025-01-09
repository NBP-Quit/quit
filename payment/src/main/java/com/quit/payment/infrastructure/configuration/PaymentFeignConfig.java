package com.quit.payment.infrastructure.configuration;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class PaymentFeignConfig {

    private final PaymentProperties paymentProperties;

    @Bean
    public RequestInterceptor paymentAuthInterceptor() {
        return new PaymentAuthInterceptor(paymentProperties);
    }

}
