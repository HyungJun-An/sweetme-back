package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.auth.repository.UserRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class ProfileServiceTests2 {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private StackRepository stackRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User mockUser;
    private Stack stack1;
    private Stack stack2;
    private Position position1;
    private Position position2;

    @BeforeEach
    void setUp() {
        // Mock User 설정
        mockUser = new User();
        mockUser.setNickname("hello");
        mockUser.setEmail("hello@naver.com");
        mockUser.setPassword(passwordEncoder.encode("1111"));
        mockUser.setLoginType(User.LoginType.NAVER);
        mockUser.setStatus(User.UserStatus.ACTIVE);
        mockUser = userRepository.save(mockUser);

        // Test용 Stack과 Position 데이터 생성
        stack1 = stackRepository.findById(1L).orElseThrow();
        stack2 = stackRepository.findById(2L).orElseThrow();
        position1 = positionRepository.findById(1L).orElseThrow();
        position2 = positionRepository.findById(2L).orElseThrow();
    }

    @Test
    @DisplayName("빈 프로필 생성 테스트")
    void createEmptyProfileTest() {
        // when
        Profile createdProfile = profileService.createEmptyProfile(mockUser);

        // then
        assertNotNull(createdProfile);
        assertEquals("", createdProfile.getDescription());
        assertEquals("", createdProfile.getProfileUrl());
        assertEquals("", createdProfile.getImagePath());
        assertTrue(createdProfile.getStacks().isEmpty());
        assertTrue(createdProfile.getPositions().isEmpty());
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void readProfileTest() {
        // given
        Profile profile = profileService.createEmptyProfile(mockUser);

        // when
        ProfileDTO readProfile = profileService.readProfile(profile.getId());

        // then
        assertNotNull(readProfile);
        assertEquals(profile.getId(), readProfile.getProfileId());
    }

    @Test
    @DisplayName("내 프로필 조회 테스트")
    void readMyProfileTest() {
        // given
        log.info(">>> Created User ID: " + mockUser.getId());

        Profile profile = profileService.createEmptyProfile(mockUser);
        log.info(">>> Created Profile ID: " + profile.getId());
        log.info(">>> Profile's User ID: " + profile.getUser().getId());

        // 프로필이 실제로 저장되었는지 확인
        Profile savedProfile = profileRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("프로필이 저장되지 않았습니다."));
        log.info(">>> Saved Profile's User ID: " + savedProfile.getUser().getId());

        // User 로 Profile 을 조회할 수 있는지 확인
        Profile foundProfile = profileRepository.findWithPositionsByUserId(mockUser.getId())
                .orElseThrow(() -> new RuntimeException("유저 id 로 프로필을 찾을 수 없습니다"));

        // when
        ProfileDTO myProfile = profileService.readMyProfile(mockUser.getId());

        // then
        assertNotNull(myProfile);
        assertEquals(profile.getId(), myProfile.getProfileId());
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void updateMyProfileTest() {
        // given
        Profile profile = profileService.createEmptyProfile(mockUser);
        ProfileDTO updateDto = createUpdateProfileDTO(profile.getId());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(mockUser.getId());
        userDTO.setNickname("testUser");
        userDTO.setEmail("test@email.com");

        // when
        profileService.updateMyProfile(updateDto, userDTO);
        ProfileDTO updatedProfile = profileService.readProfile(profile.getId());

        // then
        assertEquals("Updated description", updatedProfile.getDescription());
        assertEquals("https://example.com", updatedProfile.getProfileUrl());
        assertEquals("profile/image.jpg", updatedProfile.getImagePath());
        assertEquals(2, updatedProfile.getStackDTOS().size());
        assertEquals(2, updatedProfile.getPositionDTOS().size());
    }

    @Test
    @DisplayName("프로필 삭제 테스트")
    void deleteProfileTest() {
        // given
        Profile profile = profileService.createEmptyProfile(mockUser);

        // when
        profileService.deleteProfile(profile.getId());

        // then
        assertThrows(EntityNotFoundException.class, () -> {
            profileService.readProfile(profile.getId());
        });
    }

    @Test
    @DisplayName("프로필 옵션 조회 테스트")
    void getProfileOptionsTest() {
        // when
        Map<String, Object> options = profileService.getProfileOptions();

        // then
        assertNotNull(options);
        assertTrue(options.containsKey("stackDTOList"));
        assertTrue(options.containsKey("positionDTOList"));

        List<StackDTO> stacks = (List<StackDTO>) options.get("stackDTOList");
        List<PositionDTO> positions = (List<PositionDTO>) options.get("positionDTOList");

        assertFalse(stacks.isEmpty());
        assertFalse(positions.isEmpty());
    }

    @Test
    @DisplayName("잘못된 프로필 ID로 조회시 예외 발생 테스트")
    void readProfileWithInvalidIdTest() {
        assertThrows(EntityNotFoundException.class, () -> {
            profileService.readProfile(999L);
        });
    }

    @Test
    @DisplayName("권한이 없는 사용자의 프로필 수정 시도시 예외 발생 테스트")
    void updateProfileWithoutPermissionTest() {
        // given
        Profile profile = profileService.createEmptyProfile(mockUser);
        ProfileDTO updateDto = createUpdateProfileDTO(profile.getId());
        UserDTO unauthorizedUser = new UserDTO();
        unauthorizedUser.setId(999L);
        unauthorizedUser.setNickname("unauthorized");
        unauthorizedUser.setEmail("unauthorized@email.com");

        // when & then
        assertThrows(AccessDeniedException.class, () -> {
            profileService.updateMyProfile(updateDto, unauthorizedUser);
        });
    }

    private ProfileDTO createUpdateProfileDTO(Long profileId) {
        ProfileDTO dto = new ProfileDTO();
        dto.setProfileId(profileId);
        dto.setDescription("Updated description");
        dto.setProfileUrl("https://example.com");
        dto.setImagePath("profile/image.jpg");

        UserDTO mockUserDTO = new UserDTO();
        mockUserDTO.setId(mockUser.getId());
        mockUserDTO.setEmail("test@email.com");
        mockUserDTO.setNickname("testUser");
        dto.setUserDTO(mockUserDTO);

        List<StackDTO> stackDTOs = Arrays.asList(
                StackDTO.from(stack1),
                StackDTO.from(stack2)
        );

        List<PositionDTO> positionDTOs = Arrays.asList(
                PositionDTO.from(position1),
                PositionDTO.from(position2)
        );

        dto.setStackDTOS(stackDTOs);
        dto.setPositionDTOS(positionDTOs);

        return dto;
    }
}
