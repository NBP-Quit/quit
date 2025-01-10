package com.quit.user.application.service;

import com.quit.user.application.dto.RequestRoleDto;
import com.quit.user.application.dto.RequestStatusDto;
import com.quit.user.application.dto.UserDto;
import com.quit.user.domain.enums.RequestStatus;
import com.quit.user.domain.enums.UserRoleEnum;
import com.quit.user.domain.model.RequestRole;
import com.quit.user.domain.model.User;
import com.quit.user.infrastructure.repository.RequestRoleRepository;
import com.quit.user.infrastructure.repository.UserRepository;
import com.quit.user.presentation.request.RoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RequestRoleRepository requestRoleRepository;

    //사용자 전체 조회
    public List<UserDto> getUserList(String role){
        if ( UserRoleEnum.fromRole(role) == UserRoleEnum.MASTER) {
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

//    권한 요청
    public RequestRoleDto requestRole(String userId, String role, @Valid RoleRequest roleRequest) {
        if (UserRoleEnum.fromRole(role) == UserRoleEnum.USER) {
            RequestRole requestRole = RequestRole.create(Long.valueOf(userId), roleRequest.requestRole());

            requestRoleRepository.save(requestRole);

            return RequestRoleDto.of(requestRole);
        }else {
            throw new IllegalArgumentException("권한이 이미 존재합니다.");
        }

    }

//    MASTER 권한 or 해당 id를 갖는 USER 판별
    private boolean hasAccess(String role, String userId, Long id) {
        UserRoleEnum userRole = UserRoleEnum.fromRole(role);

        return userRole == UserRoleEnum.MASTER ||
                (userRole == UserRoleEnum.USER && userId.equals(id.toString()));
    }

//    권한 요청 조회
    public List<RequestRoleDto> getRequestRoles(String userId, String role, RequestStatus status) {
        if ( UserRoleEnum.fromRole(role) == UserRoleEnum.MASTER) {
            List<RequestRole> requestRoleList = requestRoleRepository.findByStatus(status);

            List<RequestRoleDto> requestRoleDtos = new ArrayList<>();
            for (RequestRole requestRole : requestRoleList) {
                requestRoleDtos.add(RequestRoleDto.of(requestRole));
            }
            return requestRoleDtos;

        }else {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

//    권한 수정
    @Transactional
    public UserDto changeRole(String userId, String role, UUID requestRoleId, RequestStatusDto status) {
        if ( UserRoleEnum.fromRole(role) == UserRoleEnum.MASTER) {
            RequestRole requestRole = requestRoleRepository.findById(requestRoleId).orElseThrow(
                    () -> new IllegalArgumentException("해당 id의 요청이 존재하지 않습니다.")
            );
            User user = userRepository.findById(requestRole.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("요청한 회원이 존재하지 않습니다.")
            );

            if (status.getStatus() == RequestStatus.APPROVED) {
                user.updateRole(requestRole.getRequestRole());
                requestRole.update(status.getStatus(), Long.valueOf(userId));

                return UserDto.of(user);
            }else{
                requestRole.update(status.getStatus(), Long.valueOf(userId));
                return UserDto.of(user);
            }

        }else{
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
