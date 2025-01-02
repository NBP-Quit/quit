package com.quit.user.domain.model;

import com.quit.user.common.model.BaseEntity;
import com.quit.user.domain.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "birthdate", nullable = true)
    private String birthdate;

    @Column(name = " address", nullable = true)
    private String address;

    public static User create(String email,
                              String password,
                              String nickname,
                              UserRoleEnum role,
                              String phone,
                              String birthdate,
                              String address) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(role)
                .phone(phone)
                .birthdate(birthdate)
                .address(address)
                .build();
    }
}
