package com.sweetme.back.auth.dto;

import com.sweetme.back.common.util.JWTUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sweetme.back.auth.domain.User.*;

@Getter
@Setter
@ToString
public class UserDTO{

    private Long id;
    private String email;
    private String nickname;
    private UserStatus status;
    private UserRole role;

    public static UserDTO from(com.sweetme.back.auth.domain.User user) {
        /*
        * public static LocationDTO from(Location location) {
        if (location == null) return null;

        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setType(location.getType());
        return dto;
    }
        * */

        return null;
    }
}
