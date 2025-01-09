package com.quit.payment.infrastructure.configuration;

import com.quit.payment.presentation.exception.CustomException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;

import static com.quit.payment.presentation.exception.ErrorType.*;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new CustomException(FEIGN_CLIENT_INVALID_REQUEST);
            case 404 -> new CustomException(FEIGN_CLIENT_RESOURCE_NOT_FOUND);
            case 429 -> new RetryableException(
                    response.status(),
                    response.reason(),
                    response.request().httpMethod(),
                    (Long) null,
                    response.request()
            );
            default -> new CustomException(FEIGN_CLIENT_UNKNOWN_ERROR);
        };
    }

}
