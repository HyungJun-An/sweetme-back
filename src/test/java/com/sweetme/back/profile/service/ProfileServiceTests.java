package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.auth.service.UserService;
import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.Stack;
import com.sweetme.back.profile.dto.PositionDTO;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.dto.StackDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.ProfileRepository;
import com.sweetme.back.profile.repository.StackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
@Transactional // 테스트 메서드 종료 시 자동으로 롤백
class ProfileServiceTests {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Profile testProfile;
    private ProfileDTO testProfileDTO;
    private AuthUserDTO testAuthUserDTO;
    private UserDTO testUserDTO;
    private Stack testStack;
    private Position testPosition;

    @BeforeEach
    void setUp() {
        // 테스트 유저 생성 및 저장
        testUser = new User();
        testUser.setEmail("test@kakao.com");
        testUser.setPassword(passwordEncoder.encode("1234"));
        testUser.setNickname("테스터");
        testUser.setLoginType(User.LoginType.KAKAO);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setRole(User.UserRole.ROLE_USER);
        testUser = userRepository.save(testUser);

        // 기존에 저장된 스택과 포지션 데이터 조회
        testStack = stackRepository.findAll().get(0); // 첫 번째 스택 사용
        testPosition = positionRepository.findAll().get(0); // 첫 번째 포지션 사용

        // 테스트 프로필 생성 및 저장
        testProfile = new Profile();
        testProfile.setUser(testUser);
        testProfile.setDescription("");
        testProfile.setProfileUrl("");
        testProfile.setImagePath("");
        testProfile.setStacks(new ArrayList<>());
        testProfile.setPositions(new ArrayList<>());
        testProfile = profileRepository.save(testProfile);

        // 테스트 프로필 DTO 설정
        testProfileDTO = new ProfileDTO();
        testProfileDTO.setProfileId(testProfile.getId());
        testProfileDTO.setUserDTO(UserDTO.from(testUser));
        testProfileDTO.setDescription("테스트 자기소개");
        testProfileDTO.setProfileUrl("http://test.com");
        testProfileDTO.setImagePath("test/image");
        testProfileDTO.setStackDTOS(List.of(StackDTO.from(testStack)));
        testProfileDTO.setPositionDTOS(List.of(PositionDTO.from(testPosition)));

        // 테스트용 인증용 사용자 DTO 설정
        testAuthUserDTO = new AuthUserDTO(
                testUser.getId(), testUser.getEmail(), testUser.getPassword(), testUser.getNickname(), testUser.getLoginType(), testUser.getStatus(), testUser.getRole()
        );

        // 테스트용 일반용 DTO
        testUserDTO = UserDTO.from(testUser);
    }

    @Test
    @DisplayName("새 프로필 생성 테스트")
    void createEmptyProfile() {
        // when
        Profile createdProfile = profileService.createEmptyProfile(testUser);

        // then
        assertNotNull(createdProfile);
        assertEquals(testUser.getId(), createdProfile.getUser().getId());
        assertEquals("", createdProfile.getDescription());
        assertTrue(createdProfile.getStacks().isEmpty());
        assertTrue(createdProfile.getPositions().isEmpty());
    }

    @Test
    @DisplayName("내 프로필 조회 테스트")
    void readMyProfile() {
        // when
        ProfileDTO foundProfile = profileService.readMyProfile(testUser.getId());

        // then
        assertNotNull(foundProfile);
        assertEquals(testProfile.getUser().getId(), foundProfile.getUserDTO().getId());
        assertEquals(testProfile.getDescription(), foundProfile.getDescription());
    }

    @Test
    @DisplayName("존재하지 않는 회원 아이디로 조회 시 예외 발생 테스트")
    void readMyProfile_NotFound() {
        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            profileService.readMyProfile(9999L);
        });
    }

    @Test
    @DisplayName("잘못된 프로필 ID로 조회 시 예외 발생 테스트")
    void readMyProfile_InvalidId() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            profileService.readProfile(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            profileService.readProfile(-1L);
        });
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void readProfile() {
        // when
        ProfileDTO foundProfile = profileService.readProfile(testProfile.getId());

        // then
        assertNotNull(foundProfile);
        assertEquals(testProfile.getUser().getId(), foundProfile.getUserDTO().getId());
        assertEquals(testProfile.getDescription(), foundProfile.getDescription());
    }

    @Test
    @DisplayName("존재하지 않는 프로필 조회 시 예외 발생 테스트")
    void readProfile_NotFound() {
        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            profileService.readProfile(9999L);
        });
    }

    @Test
    @DisplayName("잘못된 프로필 ID로 조회 시 예외 발생 테스트")
    void readProfile_InvalidId() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            profileService.readProfile(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            profileService.readProfile(-1L);
        });
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void updateMyProfile() {
        // given
        UserDTO testUserDTO = UserDTO.from(testUser);

        // when
        profileService.updateMyProfile(testProfileDTO, testUserDTO);

        // then
        Profile updatedProfile = profileRepository.findById(testProfile.getId()).orElseThrow();
        assertEquals(testProfileDTO.getDescription(), updatedProfile.getDescription());
        assertEquals(testProfileDTO.getProfileUrl(), updatedProfile.getProfileUrl());
        assertEquals(testProfileDTO.getImagePath(), updatedProfile.getImagePath());
        assertEquals(1, updatedProfile.getStacks().size());
        assertEquals(1, updatedProfile.getPositions().size());
        assertEquals(testStack.getId(), updatedProfile.getStacks().get(0).getId());
        assertEquals(testPosition.getId(), updatedProfile.getPositions().get(0).getId());
    }

    @Test
    @DisplayName("권한 없는 사용자의 프로필 수정 시도 테스트")
    void updateMyProfile_AccessDenied() {
        // given
        User otherUser = userRepository.findById(2L).orElseThrow();
        UserDTO otherUserDTO = UserDTO.from(otherUser);

        // when & then
        assertThrows(AccessDeniedException.class, () -> {
            profileService.updateMyProfile(testProfileDTO, otherUserDTO);
        });
    }

    @Test
    @DisplayName("프로필 정보 검증 실패 테스트")
    void updateMyProfile_ValidationFail() {
        // given
        // 잘못된 URL 형식
        testProfileDTO.setProfileUrl("invalid-url");

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            profileService.updateMyProfile(testProfileDTO, testUserDTO);
        });
    }

    @Test
    @DisplayName("프로필 삭제 테스트")
    void deleteProfile() {
        // when
        profileService.deleteProfile(testProfile.getId());

        // then
        assertFalse(profileRepository.findById(testProfile.getId()).isPresent());
    }

    @Test
    @DisplayName("프로필 옵션 조회 테스트")
    void getProfileOptions() {
        // when
        Map<String, Object> options = profileService.getProfileOptions();

        // then
        assertNotNull(options);
        assertTrue(options.containsKey("stacks"));
        assertTrue(options.containsKey("positions"));

        List<Stack> stacks = (List<Stack>) options.get("stacks");
        List<Position> positions = (List<Position>) options.get("positions");

        assertFalse(stacks.isEmpty());
        assertFalse(positions.isEmpty());
        assertTrue(stacks.stream().anyMatch(s -> s.getId().equals(testStack.getId())));
        assertTrue(positions.stream().anyMatch(p -> p.getId().equals(testPosition.getId())));
    }
}