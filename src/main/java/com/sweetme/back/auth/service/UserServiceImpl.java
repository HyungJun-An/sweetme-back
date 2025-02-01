package com.sweetme.back.auth.service;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthUserDTO getSocialUser(String social, String accessToken) {


        String email = switch (social.toLowerCase()) {
            case "kakao" -> getEmailFromKakaoAccessToken(accessToken);
            case "naver" -> getEmailFromNaverAccessToken(accessToken);
            default -> throw new IllegalArgumentException("Unsupported social login type: " + social);
        };

        log.info("email: " + email);

        User user = userRepository.findUserByEmail(email);

        if (user == null) { // 회원이 아니었다면
            log.info("신규 회원입니다.");
            User newUser = makeSocialUser(email);
            newUser.setLoginType(User.LoginType.valueOf(social.toUpperCase()));
            userRepository.save(newUser);

            return entityToDTO(newUser);
        }

        // 기존 회원일 경우
        log.info("기존 회원입니다.");
        return entityToDTO(user);
    }

    private String getEmailFromKakaoAccessToken(String accessToken) {

        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        // access token 유무 다시 확인
        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        // REST API 템플릿 객체
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청문 객체
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(
                uriBuilder.toString(), // path
                HttpMethod.GET, // method
                entity, // request object
                LinkedHashMap.class // response type
        );

        log.info("response: " + response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("----------------------------");
        log.info("bodyMap: " + bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");
        log.info("kakaoAccount: " + kakaoAccount);

        return kakaoAccount.get("email");
    }

    private String getEmailFromNaverAccessToken(String accessToken) {

        String naverGetUserURL = "https://openapi.naver.com/v1/nid/me";

        // access token 유무 다시 확인
        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        // REST API 템플릿 객체
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청문 객체
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(naverGetUserURL).build();

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(
                uriBuilder.toString(), // path
                HttpMethod.GET, // method
                entity, // request object
                LinkedHashMap.class // response type
        );

        log.info("response: " + response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("----------------------------");
        log.info("bodyMap: " + bodyMap);

        LinkedHashMap<String, String> naverAccount = bodyMap.get("response");
        log.info("naverAccount: " + naverAccount);

        return naverAccount.get("email");
    }

    private String makeTempPassword() {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }

        return buffer.toString();
    }

    private User makeSocialUser(String email) {

        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);

        String nickname = "소셜회원";

        User user = User.builder()
                .email(email)
                .password(tempPassword)
                .nickname(nickname)
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.ROLE_USER)
                .build();

        return user;
    }
}
