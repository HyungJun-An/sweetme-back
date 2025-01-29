package com.sweetme.back.common.util;

import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SecurityUtil {

    public static UserDTO getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        AuthUserDTO authUserDTO = (AuthUserDTO) authentication.getPrincipal();

        return (UserDTO) authentication.getPrincipal();
    }
}
