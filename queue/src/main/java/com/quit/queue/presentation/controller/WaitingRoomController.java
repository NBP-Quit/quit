package com.quit.queue.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Controller
public class WaitingRoomController {

    @Value("${gateway.baseUrl}")
    private String gatewayBaseUrl;

    @GetMapping("api/queues/waiting-room")
    public Mono<String> waitingRoomPage(@RequestParam UUID storeId, Model model) {
        model.addAttribute("storeId", storeId.toString());
        model.addAttribute("gatewayBaseUrl", gatewayBaseUrl);
        return Mono.just("waiting-room");
    }
}