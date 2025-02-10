package com.sweetme.back.common.util;

import com.sweetme.back.common.exception.CustomJWTException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
@Component
public class JWTUtil {

    private static String key;

    // 환경 변수에서 키를 주입받기 위한 생성자
    public JWTUtil(@Value("${com.sweetme.jwt.secret}") String secretKey) {
        JWTUtil.key = secretKey;
    }

    // 매개변수: jwt 에 담을 claim 데이터, jwt 유효기간(분 단위)
    public static String generateToken(Map<String, Object> valueMap, int min) {

        log.info("토큰 생성 중");

        // valueMap 의 id 를 명시적으로 Long 으로 변환
        if (valueMap.containsKey("id")) {
            Object idValue = valueMap.get("id");
            if (idValue != null) {
                valueMap.put("id", Long.valueOf(idValue.toString()));
            }
        }

//        log.info("Token id type: " + valueMap.get("id").getClass().getName());

        SecretKey key = null;

        try {
            // byte 형태의 키를 받아 HMAC-SHA 키를 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

        } catch (Exception e) {
            // 키 생성시 문제가 발생하면 예외를 던져 처리
            throw new RuntimeException(e.getMessage());
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact(); // 설정이 완료된 JWT 를 직렬화 하여 String 형태로 변환

        return jwtStr;
    }

    public static Map<String, Object> validateToken(String token) {

        log.info("토큰 검증 중");
        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증
                    .getBody();

            // claim 에서 id 를 가져와서 Long 으로 변환
            if (claim.containsKey("id")) {
                Object idValue = claim.get("id");
                if (idValue != null) {
                    claim.put("id", Long.valueOf(idValue.toString()));
                }
            }

//            log.info("Claim id 타입: " + claim.get("id").getClass().getName());

        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("잘못된 형식의 토큰입니다");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("토큰이 만료되었습니다.");
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("유효하지 않은 클레임입니다.");
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWT 처리 중 오류가 발생했습니다.");
        } catch (Exception e) {
            throw new CustomJWTException("토큰 검증 중 알 수 없는 오류가 발생했습니다.");
        }

        return claim;
    }
}
