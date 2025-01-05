package com.quit.user.domain.service;

import com.quit.user.application.dto.TokenDto;
import com.quit.user.application.dto.UserDto;
import com.quit.user.common.jwt.JwtUtil;
import com.quit.user.domain.model.User;
import com.quit.user.infrastructure.repository.UserRepository;
import com.quit.user.presentation.request.LoginRequest;
import com.quit.user.presentation.request.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Logger log = LoggerFactory.getLogger(AuthService.class);

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

    public TokenDto login(@Valid LoginRequest loginRequest) {

        // 사용자 이메일 존재 여부 확인
        User user = (User) userRepository.findByEmail(loginRequest.email()).orElseThrow(
                () -> new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.")
        );

        //비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        //토큰 발행
        TokenDto token = jwtUtil.createToken(user.getId(), user.getRole());
        log.info("======== 발행된 토큰 : " + token + "==================");

        return token;
    }
}
