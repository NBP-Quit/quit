package com.quit.user.presentation.controller;

import com.quit.user.application.dto.UserDto;
import com.quit.user.common.dto.ApiResponse;
import com.quit.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

//    전체 사용자 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUsers() {

        return ResponseEntity.ok(ApiResponse.success(userService.getUserList()));
    }

//    특정 사용자 상세 정보 조회
//    TODO
//     - MASTER가 아닌 USER 권한의 사용자가 본인 정보 확인을 위한 사용자 본인 확인 로직 추가 필요
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, userDto)));
    }

//    TODO
//     - 삭제하려는 사용자 id와 현재 로그인된 사용자 id 일치 여부 확인 필요
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }
}
