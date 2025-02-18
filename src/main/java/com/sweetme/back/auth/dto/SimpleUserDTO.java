package com.sweetme.back.auth.dto;

import com.sweetme.back.auth.domain.User;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

