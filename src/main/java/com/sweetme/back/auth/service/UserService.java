package com.sweetme.back.auth.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.AuthUserDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {

    AuthUserDTO getSocialUser(String social, String accessToken);

    default AuthUserDTO entityToDTO(User user) {

        AuthUserDTO dto = new AuthUserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getLoginType(),
                user.getStatus(),
                user.getRole()
        );

        return dto;
    }
}
