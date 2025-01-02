package com.quit.user.domain.service;

import com.quit.user.application.dto.UserDto;
import com.quit.user.domain.model.User;
import com.quit.user.infrastructure.repository.UserRepository;
import com.quit.user.presentation.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserDto signup(@Valid SignupRequest signupRequest) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        //user 객체 생성
        User user = User.create(signupRequest.email(), encodedPassword, signupRequest.nickname(),
                signupRequest.phone(), signupRequest.birthdate(), signupRequest.address());

        userRepository.save(user);

        return UserDto.of(user);

    }
}
