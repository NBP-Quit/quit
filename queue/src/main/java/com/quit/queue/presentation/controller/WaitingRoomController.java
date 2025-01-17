package com.quit.queue.presentation.controller;

import com.quit.queue.application.service.RoleValidationService;
import com.quit.queue.common.RoleValidationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WaitingRoomController {

    @Value("${gateway.baseUrl}")
    private String gatewayBaseUrl;

    private final RoleValidationService roleValidationService;

    @GetMapping("api/queues/waiting-room")
    public Mono<String> waitingRoomPage(@RequestParam UUID storeId,
                                        @RequestHeader(value = "X-User-Role") String userRole,
                                        Model model) {
        return roleValidationService.validateUserRole(userRole, RoleValidationType.USER)
                .then(Mono.defer(() -> {
                    model.addAttribute("storeId", storeId.toString());
                    model.addAttribute("gatewayBaseUrl", gatewayBaseUrl);
                    return Mono.just("waiting-room");
                }));
    }
}