package com.quit.queue.infrastructure.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StoreClient {

    private final WebClient webClient;

    @Autowired
    public StoreClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://store/api").build();
    }

}
