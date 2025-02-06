package com.sweetme.back.auth.service;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.common.exception.SocialLoginException;
import com.sweetme.back.profile.service.ProfileService;
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

import static com.sweetme.back.auth.domain.User.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    @Override
    public AuthUserDTO getSocialUser(String social, String accessToken) {


        String email = switch (social.toLowerCase()) {
            case "kakao" -> getEmailFromKakaoAccessToken(accessToken);
            case "naver" -> getEmailFromNaverAccessToken(accessToken);
            default -> throw new IllegalArgumentException("Unsupported social login type: " + social);
        };

        log.info("email: " + email);
        LoginType currentLoginType = LoginType.valueOf(social.toUpperCase());

        User user = userRepository.findUserByEmail(email);

        if (user == null) { // 회원이 아니었다면
            log.info("신규 회원입니다.");
            User newUser = makeSocialUser(email);
            newUser.setLoginType(LoginType.valueOf(social.toUpperCase()));
            userRepository.save(newUser);

            /*
            * TODO: 신규 회원일 경우 자동으로 기본값이 들어간 Profile 생성 로직 추가
            * */
            profileService.createEmptyProfile(newUser);

            return entityToDTO(newUser);
        }

        // 기존 회원이고 로그인 타입이 다를 경우
        if (!user.getLoginType().equals(currentLoginType)) {
            String message = String.format("이 메일은 %s 계정으로 가입되어 있습니다. %s로 로그인해 주세요.",
                    user.getLoginType().name().toLowerCase(),
                    user.getLoginType().getDisplayName());
            throw new SocialLoginException(user.getLoginType(), message);
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

        String nickname = "스윗미";

        User user = builder()
                .email(email)
                .password(passwordEncoder.encode("1111"))
                .nickname(nickname)
                .status(UserStatus.ACTIVE)
                .role(UserRole.ROLE_USER)
                .build();

        return user;
    }
}
