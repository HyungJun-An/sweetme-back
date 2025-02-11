package com.sweetme.back.profile.repository;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.Stack;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ProfileRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private StackRepository stackRepository;

    @Test
    public void testInsert() {
        // 등록 테스트 (신규회원 가입시 필요)
        Profile profile = new Profile();

        profile.setUser(userRepository.findUserByEmail("jwpark4132@naver.com"));
        profile.setDescription("안녕하세요.");
        profile.setProfileUrl("https://github.com/JongWook6/mastering-git-github.git");
        profile.setImagePath("https://storage/files/task1.pdf");

        List<Stack> stacks = stackRepository.findAllById(List.of(1L, 2L, 3L));
        profile.setStacks(stacks);

        List<Position> positions = positionRepository.findAllById(List.of(1L));
        profile.setPositions(positions);

        Profile savedProfile = profileRepository.save(profile);
        log.info("savedProfile: " + savedProfile);
    }

    @Test
    @Transactional
    public void testFindById() {
        // 프로필 id 로 조회시 연관 엔티티들이 함께 조회되는지 테스트
        Optional<Profile> result = profileRepository.findById(1L);
        Profile profile = result.orElseThrow();

        log.info("프로필: " + profile);
        log.info("유저: " + profile.getUser());
        log.info("스택: " + profile.getStacks());
        log.info("포지션: " + profile.getPositions());
    }

    @Test
    public void testUpdateProfile() {
        // 프로필 수정이 정상적으로 되는지 테스트
        Profile profile = profileRepository.findById(1L).orElseThrow();

        String updatedDesc = "프로필 설명이 수정되었습니다.";
        profile.setDescription(updatedDesc);

        profileRepository.save(profile);

        Profile updatedProfile = profileRepository.findById(1L).orElseThrow();

        assertEquals(updatedDesc, updatedProfile.getDescription());
    }

    @Test
    public void testUpdateProfileStacks() {
        // 스택 목록 수정이 정상적으로 되는지 테스트
        Profile profile = profileRepository.findById(51L).orElseThrow();
        profile.setDescription("자기소개 수정되었습니다.");
        profile.setProfileUrl("http://test.com");
        profile.setImagePath("test/image");

        // 기존 stack 에 추가 하는 방식이 아니라 새로 교체
        List<Stack> newStacks = stackRepository.findAllById(List.of(4L, 5L));
        List<Position> newPositions = positionRepository.findAllById(List.of(1L));
        profile.setStacks(newStacks);
        profile.setPositions(newPositions);


        profileRepository.save(profile);

        Profile updatedProfile = profileRepository.findById(51L).orElseThrow();

        assertEquals(2, updatedProfile.getStacks().size());
    }

    @Test
    public void testDeleteProfile() {
        // 프로필 삭제 테스트
        Profile profile = profileRepository.findById(11L).orElseThrow();

        profileRepository.delete(profile);

        assertFalse(profileRepository.existsById(11L));
    }

    @Test
    public void testFindByUser() {
        // 특정 사용자의 프로필 조회 테스트
        User user = userRepository.findUserByEmail("lee@email.com");

        // 스택과 함께 조회
        Profile profile = profileRepository.findWithStacksByUserId(user.getId()).orElseThrow();

        // 포지션과 함께 조회
        Profile profileWithPositions = profileRepository.findWithPositionsByUserId(user.getId()).orElseThrow();

        // 병합
        profile.setPositions(profileWithPositions.getPositions());

        log.info(profile);

        assertNotNull(profile);
        assertEquals(user.getId(), profile.getUser().getId());
    }

    @Test
    public void testValidation() {
        // 필수 필드 누락 시 예외 발생 테스트
        Profile invalidProfile = new Profile();
        // user, description 은 필수 필드인데 설정하지 않음

        assertThrows(Exception.class, () -> {
            profileRepository.save(invalidProfile);
        });
    }
}