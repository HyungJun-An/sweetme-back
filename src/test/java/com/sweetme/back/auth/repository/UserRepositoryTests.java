package com.sweetme.back.auth.repository;

import com.sweetme.back.auth.domain.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sweetme.back.auth.domain.User.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testInsertUser() {

        for (int i = 0; i < 10; i++) {

            User user = builder()
                    .nickname("USER" + i)
                    .email("user" + i + "@kakao.com")
                    .password(passwordEncoder.encode("1111"))
                    .loginType(LoginType.KAKAO)
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.ROLE_USER)
                    .build();

            userRepository.save(user);
        }
    }

    @Test
    public void testRead() {

        String email = "user9@kakao.com";

        User user = userRepository.getUserByEmail(email);

        log.info("---------------");
        log.info(user);
    }


}