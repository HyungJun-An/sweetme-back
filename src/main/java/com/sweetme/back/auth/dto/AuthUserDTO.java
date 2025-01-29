package com.sweetme.back.auth.dto;

import com.sweetme.back.auth.domain.User.LoginType;
import com.sweetme.back.auth.domain.User.UserRole;
import com.sweetme.back.auth.domain.User.UserStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class AuthUserDTO extends User {

    private Long id;
    private String email;
    private String nickname;
    private LoginType loginType;
    private UserStatus status;
    private UserRole role;

    public AuthUserDTO(Long id, String email, String password, String nickname,
                       LoginType loginType, UserStatus status, UserRole role) {
        super(email, password, Collections.singleton(new SimpleGrantedAuthority(role.name())));

        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.loginType = loginType;
        this.status = status;
        this.role = role;
    }

    // JWT 생성 시 사용
    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("id", id);
        dataMap.put("email", email);
        dataMap.put("nickname", nickname);
        dataMap.put("loginType", loginType);
        dataMap.put("status", status);
        dataMap.put("role", role);

        return dataMap;
    }

    // claims 로부터 UserDTO 생성
    public static AuthUserDTO fromClaims(Map<String, Object> claims) {

        Long id = (Long) claims.get("id");
        String email = (String) claims.get("email");
        String nickname = (String) claims.get("nickname");
        LoginType loginType = LoginType.valueOf((String) claims.get("loginType"));
        UserStatus status = UserStatus.valueOf((String) claims.get("status"));
        UserRole role = UserRole.valueOf((String) claims.get("role"));

        return new AuthUserDTO(id, email, null, nickname, loginType, status, role);

    }
}

