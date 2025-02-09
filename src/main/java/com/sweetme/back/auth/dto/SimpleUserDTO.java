package com.sweetme.back.auth.dto;

import com.sweetme.back.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleUserDTO {
    private Long id;
    private String email;
    private String nickname;

    public static SimpleUserDTO from(User user) {
        return SimpleUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}

