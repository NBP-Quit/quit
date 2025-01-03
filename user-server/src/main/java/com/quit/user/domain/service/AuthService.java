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

        if (userRepository.findByEmail(signupRequest.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.findByNickname(signupRequest.nickname()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        if(userRepository.findByPhone(signupRequest.phone()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 번호입니다.");
        }

        //user 객체 생성
        User user = User.create(signupRequest.email(), encodedPassword, signupRequest.nickname(),
                signupRequest.phone(), signupRequest.birthdate(), signupRequest.address());
        userRepository.save(user);

        return UserDto.of(user);

    }
}
