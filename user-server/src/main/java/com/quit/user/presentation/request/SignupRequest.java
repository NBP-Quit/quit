package com.quit.user.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
                message = "비밀번호는 반드시 하나 이상의 대문자와 하나 이상의 소문자, 하나 이상의 숫자, 하나 이상의 특수문자가 필요합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
        String nickname,

        @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호는 XXX-XXXX-XXXX와 같은 형식으로 입력해야합니다.")
        String phone,

        @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "생일은 yyyy-MM-dd 형식이어야 합니다.")
        String birthdate,

        String address
){
}
