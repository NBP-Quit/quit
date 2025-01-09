package com.quit.user.presentation.controller;

import com.quit.user.application.dto.UserDto;
import com.quit.user.common.dto.ApiResponse;
import com.quit.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

//  전체 사용자 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUsers(@RequestHeader("X-User-Role")String role) {

        return ResponseEntity.ok(ApiResponse.success(userService.getUserList(role)));
    }

//  특정 사용자 상세 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id,
                                                        @RequestHeader("X-User-Role")String role,
                                                        @RequestHeader("X-User-Id")String userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUser(id, role, userId)));
    }
//    사용자 정보 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id,
                                                           @RequestBody UserDto userDto,
                                                           @RequestHeader("X-User-Role")String role,
                                                           @RequestHeader("X-User-Id")String userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, userDto, role, userId)));
    }

//    사용자 탈퇴 처리
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id,
                                                  @RequestHeader("X-User-Role")String role,
                                                  @RequestHeader("X-User-Id")String userId) {
        userService.deleteUser(id, role, userId);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }
}
