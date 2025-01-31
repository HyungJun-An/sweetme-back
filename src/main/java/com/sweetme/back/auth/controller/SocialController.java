package com.sweetme.back.auth.controller;

import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.service.UserService;
import com.sweetme.back.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final UserService userService;

    @GetMapping("/auth/login/kakao")
    public Map<String, Object> getUserFromKakao(String accessToken) {

        log.info("access Token: " + accessToken);

        AuthUserDTO authUserDTO = userService.getKakaoUser(accessToken);

        Map<String, Object> claims = authUserDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
