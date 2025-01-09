package com.quit.user.application.service;

import com.quit.user.application.dto.UserDto;
import com.quit.user.domain.enums.UserRoleEnum;
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

    //사용자 전체 조회
    public List<UserDto> getUserList(String role){
        if (role.equals(UserRoleEnum.MASTER.toString())) {
            List<User> users = userRepository.findAll();
            List<UserDto> userDtos = new ArrayList<>();
            for (User user : users) {
                userDtos.add(UserDto.of(user));
            }
            return userDtos;

        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

    }

//    단일 사용자 조회
    public UserDto getUser(Long id,String role, String userId) {
        if (hasAccess(role, userId, id)) {
            User user = userRepository.findById(id).orElseThrow(()
                    -> new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));

            return UserDto.of(user);
        }else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

    }

//    사용자 정보 업데이트
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto, String role, String userId) {

        if (hasAccess(role, userId, id)) {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));

            user.update(id,
                    userDto.email(),
                    userDto.nickname(),
                    userDto.phone(),
                    userDto.birthdate(),
                    userDto.address());

            return UserDto.of(user);

        } else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

//    사용자 탈퇴
    @Transactional
    public void deleteUser(Long id, String role, String userId) {
        if (hasAccess(role, userId, id)) {
            User user = userRepository.findById(id).orElseThrow(() ->
                    new IllegalArgumentException("해당하는 ID값을 갖는 사용자가 존재하지 않습니다."));
            user.markAsDeleted(String.valueOf(id));
        }
    }

    private boolean hasAccess(String role, String userId, Long id) {
        UserRoleEnum userRole = UserRoleEnum.fromRole(role);

        return userRole == UserRoleEnum.MASTER ||
                (userRole == UserRoleEnum.USER && userId.equals(id.toString()));
    }
}
