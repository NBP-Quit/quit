package com.quit.user.presentation.controller;

import com.quit.user.application.dto.TokenDto;
import com.quit.user.application.dto.UserDto;
import com.quit.user.common.dto.ApiResponse;
import com.quit.user.domain.service.AuthService;
import com.quit.user.presentation.request.LoginRequest;
import com.quit.user.presentation.request.SignupRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<UserDto>> signup(@Valid @RequestBody SignupRequest signupRequest) {

        UserDto createdUser = authService.signup(signupRequest);

        return ResponseEntity
                .ok(ApiResponse.success(createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            TokenDto token = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(token));
        } catch (Exception e) {
            throw new IllegalArgumentException("로그인에 실패했습니다. 아이디와 비밀번호를 다시 확인해주세요.", e);
        }
    }
}

