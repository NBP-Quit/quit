package com.quit.user.presentation.controller;

import com.quit.user.application.dto.UserDto;
import com.quit.user.common.dto.ApiResponse;
import com.quit.user.domain.service.AuthService;
import com.quit.user.presentation.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody SignupRequest signupRequest) {

        UserDto createdUser = authService.signup(signupRequest);

        return ApiResponse.success(createdUser);
    }
}

