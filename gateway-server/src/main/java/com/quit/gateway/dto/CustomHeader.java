package com.quit.gateway.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomHeader {
    private String token;
    private String id;
    private String email;
    private String nickname;
    private String role;


}
