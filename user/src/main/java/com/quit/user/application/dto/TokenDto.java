package com.quit.user.application.dto;

public record TokenDto(
        String accessToken
) {
   public static TokenDto of(final String accessToken) {
       return new TokenDto(accessToken);
   }
}
