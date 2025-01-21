package com.sweetme.back.auth.dto;

import com.sweetme.back.auth.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String email;
    private String nickname;
    // 필요한 기본적인 사용자 정보만 포함
    // 비밀번호와 같은 민감한 정보는 제외

    public static UserDTO from(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        return dto;
    }
}
