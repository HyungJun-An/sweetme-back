package com.sweetme.back.auth.controller;

import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.service.UserService;
import com.sweetme.back.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/auth/login/{social}")
    public Map<String, Object> getUserFromSocial(@PathVariable String social, String accessToken) {

        log.info("social: " + social);
        log.info("access Token: " + accessToken);

        AuthUserDTO authUserDTO = userService.getSocialUser(social, accessToken);

        Map<String, Object> claims = authUserDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    @PostMapping("/auth/login/naver")
    public Map<String, Object> getNaverUser(@RequestBody Map<String, String> request) {
        String grantType = request.get("grant_type");
        String clientId = request.get("client_id");
        String clientSecret = request.get("client_secret");
        String code = request.get("code");
        String state = request.get("state");

        String access_token_url = "https://nid.naver.com/oauth2.0/token";
        String params = "grant_type=" + grantType
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&state=" + state;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(params, headers);
        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(access_token_url, HttpMethod.POST, entity, LinkedHashMap.class);

        log.info("response: " +response);

        LinkedHashMap<String, String> bodyMap = response.getBody();
        log.info("bodyMap: " + bodyMap);

        String accessToken = bodyMap.get("access_token");
        log.info("accessToken: " + accessToken);

        // UserService 이용
        AuthUserDTO authUserDTO = userService.getSocialUser("naver", accessToken);

        Map<String, Object> claims = authUserDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
