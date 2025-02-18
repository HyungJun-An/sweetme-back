package com.sweetme.back.auth.dto;

import com.sweetme.back.common.util.JWTUtil;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sweetme.back.auth.domain.User.*;

@Getter
@Setter
@Log4j2
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{ // 일반용 DTO

    private Long id;
    private String email;
    private String nickname;
    private UserStatus status;
    private UserRole role;

    // 엔티티에서 일반용 DTO 생성
    public static UserDTO from(com.sweetme.back.auth.domain.User user) {
        if (user == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setNickname(user.getNickname());
        userDTO.setStatus(user.getStatus());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    // 인증용 DTO 에서 일반용 DTO 생성
    public static UserDTO fromAuthDTO(AuthUserDTO authUserDTO) {
        if (authUserDTO == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(authUserDTO.getId());
        userDTO.setEmail(authUserDTO.getEmail());
        userDTO.setNickname(authUserDTO.getNickname());
        userDTO.setStatus(authUserDTO.getStatus());
        userDTO.setRole(authUserDTO.getRole());

        return userDTO;
    }

    // JWT 생성 시 사용
    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", id);
        dataMap.put("email", email);
        dataMap.put("nickname", nickname);
        dataMap.put("status", status);
        dataMap.put("role", role);

        return dataMap;
    }

    // claims 로부터 UserDTO 생성 시 사용
    public static UserDTO fromClaims(Map<String, Object> claims) {

        try {
            // 각 값을 가져오면서 로그 출력
            Object idObj = claims.get("id");
            Object emailObj = claims.get("email");
            Object nicknameObj = claims.get("nickname");
            Object statusObj = claims.get("status");
            Object roleObj = claims.get("role");

//            log.info("ID: {} (type: {})", idObj, (idObj != null ? idObj.getClass() : "null"));
//            log.info("Email: {}", emailObj);
//            log.info("Nickname: {}", nicknameObj);
//            log.info("Status: {}", statusObj);
//            log.info("Role: {}", roleObj);

            // 값 변환
            Long id = (Long) claims.get("id");
            String email = (String) claims.get("email");
            String nickname = (String) claims.get("nickname");
            UserStatus status = UserStatus.valueOf((String) claims.get("status"));
            UserRole role = UserRole.valueOf((String) claims.get("role"));

//            log.info(id);
//            log.info(email);
//            log.info(nickname);
//            log.info(status);
//            log.info(role);

            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setEmail(email);
            userDTO.setNickname(nickname);
            userDTO.setStatus(status);
            userDTO.setRole(role);

            return userDTO;

        } catch (Exception e) {
            log.error("error in fromClaims: {}", e.getMessage());
            log.error("Stack trace: ", e);
            throw e;
        }
    }
}
