package com.quit.user.domain.service;

import com.quit.user.application.dto.UserDto;
import com.quit.user.domain.model.User;
import com.quit.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUserList(){
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for(User user : users){
            userDtos.add(UserDto.of(user));
        }

        return userDtos;
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));

        return UserDto.of(user);
    }

//    TODO
//     - 수정하려고 하는 사용자정보가 현재 로그인된 사용자 본인인지 확인 로직 필요
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));

        user.update(id,
                userDto.email(),
                userDto.nickname(),
                userDto.phone(),
                userDto.birthdate(),
                userDto.address());

        return UserDto.of(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));

        user.markAsDeleted(String.valueOf(id));
    }
}
