package com.quit.review.infrastructure.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {
	Long id;
	String email;
	String nickname;
	String phone;
	String birthdate;
	String address;
}
