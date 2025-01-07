package com.quit.payment.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "payment")
@Getter
@Setter
public class PaymentProperties {
    private String secretKey;
    private String baseUrl;
}
