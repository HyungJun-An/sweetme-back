package com.sweetme.back;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.studygroup.domain.Location;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.dto.StudyUpdateRequest;
import com.sweetme.back.studygroup.repository.LocationRepository;
import com.sweetme.back.studygroup.repository.StudyRepository;
import com.sweetme.back.studygroup.service.StudyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StudyTest {

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserRepository userRepository; // 테스트용 사용자 생성에 필요

    @Autowired
    private LocationRepository locationRepository; // 위치 정보 필요시
    @Autowired
    private StudyRepository studyRepository;

    @Test
    @DisplayName("스터디방 생성 성공 테스트")
    void createStudySuccess() {
        //given
//        User leader = createTestUser(); // 테스트용 사용자 생성
//        Location location = createTestLocation(); // 테스트용 위치 생성

        StudyCreateRequest request = StudyCreateRequest.builder()
                .title("테스트 스터디")
                .description("테스트 스터디 설명")
                .isOnline(false)
                .type(Study.StudyType.STUDY)
                .minCapacity((byte) 2)
                .maxCapacity((byte) 8)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusMonths(1))
                .locationId(1L)
                .positionIds(List.of(1L))
                .stackIds(List.of(1L))
                .build();

        // when
        Study createdStudy = studyService.createStudy(request);

        // then
        assertNotNull(createdStudy.getId());
        assertEquals(request.getTitle(), createdStudy.getTitle());
        assertEquals(request.getDescription(), createdStudy.getDescription());
        assertEquals(request.getIsOnline(), createdStudy.getIsOnline());
        assertEquals(request.getType(), createdStudy.getType());
        assertEquals(request.getMinCapacity(), createdStudy.getMinCapacity());
        assertEquals(request.getMaxCapacity(), createdStudy.getMaxCapacity());
        assertEquals((Long) 4L, createdStudy.getLeader().getId()); // 하드코딩된 리더 ID 검증
    }

    @Test
    @DisplayName("스터디 수정 성공 테스트")
    void updateStudySuccess() {
        //given
        Study study = createTestStudy(); // 테스트용 스터디 설정

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .title("수정된 제목")
                .description("수정된 설명")
                .isOnline(false)
                .type(Study.StudyType.PROJECT)
                .minCapacity((byte) 2)
                .maxCapacity((byte) 2)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusMonths(1))
                .locationId(1L)
                .build();

        //when
        Study updateStudy = studyService.updateStudy(study.getId(), request);

        //then
        assertAll(
                () -> assertEquals(request.getTitle(), updateStudy.getTitle()),
                () -> assertEquals(request.getDescription(), updateStudy.getDescription()),
                () -> assertEquals(request.getIsOnline(), updateStudy.getIsOnline()),
                () -> assertEquals(request.getType(), updateStudy.getType()),
                () -> assertEquals(request.getMinCapacity(), updateStudy.getMinCapacity()),
                () -> assertEquals(request.getMaxCapacity(), updateStudy.getMaxCapacity()),
                () -> assertEquals(request.getStartAt(), updateStudy.getStartAt()),
                () -> assertEquals(request.getEndAt(), updateStudy.getEndAt()),
                () -> assertEquals(request.getLocationId(), updateStudy.getLocation().getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 스터디 수정 실패 테스트")
    void updateNonExistingStudyFail() {
        //given
        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .title("수정된 제목")
                .build();

        // when & then
        assertThrows(IllegalArgumentException.class, () ->
                studyService.updateStudy(9999L, request));
    }

    @Test
    @DisplayName("마감된 스터디 수정 실패 테스트")
    void updateClosedStudyFail() {
        //given
        Study study = createTestStudy();
        study.setIsOpened(false);
        studyRepository.save(study);

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .title("수정된 제목")
                .build();

        // when && then
        assertThrows(IllegalStateException.class, () ->
                studyService.updateStudy(study.getId(), request));
    }

    private Study createTestStudy() {
        Location location = Location.builder()
                .name("서울시")
                .type(Location.LocationType.CITY)
                .build();
        locationRepository.save(location);

        User leader = userRepository.save(User.builder()
                .email("test@email.com")
                .loginType(User.LoginType.EMAIL)
                .nickname("test")
                .password("1234")
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.ROLE_USER)
                .build());

        Study study = Study.builder()
                .leader(leader)
                .title("테스트 스터디")
                .description("테스트 설명")
                .isOnline(true)
                .type(Study.StudyType.STUDY)
                .minCapacity((byte) 2)
                .maxCapacity((byte) 8)
                .startAt(LocalDateTime.now().plusDays(1))
                .endAt(LocalDateTime.now().plusMonths(1))
                .isOpened(true)
                .location(location)
                .views(0)
                .build();

        return studyRepository.save(study);
    }

    @Test
    @DisplayName("스터디방 삭제 성공 테스트")
    void deleteStudySuccess() {
        // given
        Study study = createTestStudy(); // 테스트용 스터디 생성
        Long studyId = study.getId();

        // when
        studyService.deleteStudy(studyId);

        // then
        assertFalse(studyRepository.existsById(studyId));
    }

    @Test
    @DisplayName("존재하지 않는 스터디 삭제 실패 테스트")
    void delteNonExistingStudyFail(){
        //given
        Long nonExistingStudyId = 9999L;

        // when & then
        assertThrows(EntityNotFoundException.class, () ->
                studyService.deleteStudy(nonExistingStudyId));
    }
}
//    // 테스트용 사용자 생성 헬퍼 메소드
//    private User createTestUser() {
//        User user = User.builder()
//                .email("test@example.com")
//                .password("password")
//                .nickname("테스트유저")
//                .build();
//        return userRepository.save(user);
//    }
//
//    // 테스트용 위치 생성 헬퍼 메소드
//    private Location createTestLocation() {
//        Location location = Location.builder()
//                .name("서울시")
//                .type(Location.LocationType.CITY)
//                .build();
//        return locationRepository.save(location);
//    }

