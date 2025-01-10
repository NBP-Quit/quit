package com.quit.queue.infrastructure.client;

import com.quit.queue.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class StoreClient {

    private final WebClient webClient;

    @Autowired
    public StoreClient(WebClient.Builder webClientBuilder, @Value("${store.baseUrl}") String baseUrl) {
        baseUrl = baseUrl.replaceAll("^\"|\"$", "");
        log.info("Injected baseUrl: " + baseUrl);
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<ApiResponse<Boolean>> getStoreForInternal(UUID storeId) {
        return webClient.get()
                .uri("/{storeId}/internal", storeId)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.createException().flatMap(Mono::error)
                )
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
