package com.sweetme.back.auth.controller;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.common.exception.CustomJWTException;
import com.sweetme.back.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sweetme.back.auth.domain.User.*;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    @RequestMapping("/auth/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {

        log.info("APIRefreshController");

        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }
        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7);

        // AccessToken 이 만료되지 않았다면
        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // RefreshToken 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        // 토큰의 claim 에서 필요한 내용만 추출해서 newClaims 생성
        Map<String, Object> newClaims = getNewClaims(claims);

        log.info("refresh ... claims: " + newClaims);

        String newAccessToken = JWTUtil.generateToken(newClaims, 10);

        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(newClaims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

    }

    private boolean checkExpiredToken(String token) {

        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException e) {
            if (e.getMessage().equals("토큰이 만료되었습니다.")) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTime(Integer exp) {

        // JWT exp 를 날짜로 변환
        Date expDate = new Date((long) exp * 1000);

        // 현재 시간과의 차이 계산 (밀리초)
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분단위 계산
        long leftMin = gap / (1000 * 60);

        // 1시간이 남았는가
        return leftMin < 60;
    }

    private Map<String, Object> getNewClaims(Map<String, Object> claims) {

        Map<String, Object> newClaims = new HashMap<>();

        String email = (String) claims.get("email");
        String password = (String) claims.get("password");
        String nickname = (String) claims.get("nickname");
        LoginType loginType = LoginType.valueOf((String) claims.get("loginType"));
        UserStatus status = UserStatus.valueOf((String) claims.get("status"));
        UserRole role = UserRole.valueOf((String) claims.get("role"));

        newClaims.put("email", email);
        newClaims.put("password", password);
        newClaims.put("nickname", nickname);
        newClaims.put("loginType", loginType);
        newClaims.put("status", status);
        newClaims.put("role", role);

        return newClaims;
    }
}
