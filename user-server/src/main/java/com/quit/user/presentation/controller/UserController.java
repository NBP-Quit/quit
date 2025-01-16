package com.quit.user.presentation.controller;

import com.quit.user.application.dto.RequestRoleDto;
import com.quit.user.application.dto.RequestStatusDto;
import com.quit.user.application.dto.UserDto;
import com.quit.user.common.dto.ApiResponse;
import com.quit.user.application.service.UserService;
import com.quit.user.domain.enums.RequestStatus;
import com.quit.user.domain.enums.UserRoleEnum;
import com.quit.user.infrastructure.repository.RequestRoleRepository;
import com.quit.user.presentation.request.RoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email,
                                                               @RequestHeader("X-User-Role")String role,
                                                               @RequestHeader("X-User-Id")String userId){

        return ResponseEntity.ok(ApiResponse.success(userService.getUserByEmail(email)));
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

//    권한 요청
    @PostMapping("/request-role")
    public ResponseEntity<ApiResponse<RequestRoleDto>> requestRole(@RequestHeader("X-User-Role")String role,
                                                   @RequestHeader("X-User-Id")String userId,
                                                   @Valid @RequestBody RoleRequest roleRequest)  {

        return ResponseEntity.ok(ApiResponse.success(userService.requestRole(userId, role, roleRequest)));
    }

//    권한 요청 확인
    @GetMapping("/request-role")
    public ResponseEntity<ApiResponse<?>> getRequestRoles(@RequestHeader("X-User-Role")String role,
                                                          @RequestHeader("X-User-Id")String userId,
                                                          @RequestParam("status") RequestStatus status) {
        return ResponseEntity.ok(ApiResponse.success(userService.getRequestRoles(userId, role, status)));
    }

//    권한 수정
    @PutMapping("/request-role/{requestRoleId}")
    public ResponseEntity<ApiResponse<UserDto>> changeRole(@RequestHeader("X-User-Role") String role,
                                                           @RequestHeader("X-User-Id") String userId,
                                                           @PathVariable UUID requestRoleId,
                                                           @RequestBody RequestStatusDto status) {
        return ResponseEntity.ok(ApiResponse.success(userService.changeRole(userId, role, requestRoleId, status)));
    }

}


