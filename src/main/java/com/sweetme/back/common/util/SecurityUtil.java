package com.sweetme.back.common.util;

import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SecurityUtil {

    // 인증 정보에서 회원 DTO 호출
    public static UserDTO getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) { // 익명 사용자 체크
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        // Principal 타입 체크 추가
        if (!(authentication.getPrincipal() instanceof UserDTO)) {
            throw new RuntimeException("Invalid principal type");
        }

        UserDTO userDTO = (UserDTO) authentication.getPrincipal();

        return userDTO;
    }
}
