package com.quit.user.infrastructure.repository;

import com.quit.user.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<Object> findByEmail(@NotBlank(message = "이메일은 필수 입력 항목입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") String email);

    Optional<Object> findByNickname(@NotBlank(message = "닉네임은 필수 입력 사항입니다.") String nickname);

    Optional<Object> findByPhone(@NotBlank(message = "전화번호는 필수 입력 사항입니다.") @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호는 XXX-XXXX-XXXX와 같은 형식으로 입력해야합니다.") String phone);
}
