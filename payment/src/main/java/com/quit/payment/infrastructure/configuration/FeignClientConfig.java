package com.quit.payment.infrastructure.configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(3, TimeUnit.SECONDS, 30, TimeUnit.SECONDS, true);
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(2000, 5000, 3);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
