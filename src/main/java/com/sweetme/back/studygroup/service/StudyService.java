package com.sweetme.back.studygroup.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.studygroup.domain.Location;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.dto.StudySearchRequest;
import com.sweetme.back.studygroup.dto.StudyUpdateRequest;
import com.sweetme.back.studygroup.repository.LocationRepository;
import com.sweetme.back.studygroup.repository.StudyRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager; // EntityManger 주입
//    private final UserRepository userRepository;
//    private final PositionRepository positionRepository;
//    private final StackRepository stackRepository;
//    private final ChatService chatService;

    // 스터디방 설정 수정
    public Study updateStudy(Long studyId, StudyUpdateRequest request) {
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Study ID"));

        // 검증로직
        validUpdateRequest(study, request);

        //기본정보 업데이트
        study.setTitle(request.getTitle());
        study.setDescription(request.getDescription());
        study.setIsOnline(request.getIsOnline());
        study.setType(request.getType());
        study.setMinCapacity(request.getMinCapacity());
        study.setMaxCapacity(request.getMaxCapacity());
        study.setStartAt(request.getStartAt());
        study.setEndAt(request.getEndAt());

        // 위치 정보 업데이트
        if (!request.getIsOnline() && request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Location ID"));
            study.setLocation(location);
        }

        return study;

    }

    private void validUpdateRequest(Study study, StudyUpdateRequest request) {
        //1. 스터디 모집이 마감되었는지 확인
        if (!study.getIsOpened()) {
            throw new IllegalStateException("이미 모집이 마감된 스터디는 수정할 수 없습니다.");
        }

        //2. 최소/최대 인원 검증
        if (request.getMaxCapacity() < request.getMinCapacity()) {
            throw new IllegalArgumentException("최대 인원은 최소 인원보다 적을 수 없습니다. ");
        }

        //3. 날짜 검증
        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new IllegalArgumentException("종료일은 시작일보다 이전일 수 없습니다.");
        }

        //4. 위치 정보 검증
        if (!request.getIsOnline() && request.getLocationId() == null) {
            throw new IllegalArgumentException("오프라인 스터디는 위치 정보가 필수입니다.");
        }
    }

    // 스터디방 필터별 검색
    public Page<Study> getStudies(StudySearchRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt") // 최신순 정렬
        );

        // 전체 조회인 경우
        if (request.isAllStudies()) {
            return studyRepository.findStudiesWithFilters(
                    null, null, null, null, pageRequest
            );
        }
        // 필터 조회인 경우
        return studyRepository.findStudiesWithFilters(
                request.getLocationId(),
                request.getIsOnline(),
                request.getType(),
                request.getIsOpened(),
                pageRequest
        );
    }

    // 전체 스터디 조회
    public Page<Study> getAllStudies(StudySearchRequest request) {
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "id") // 최신순 정렬
        );
        return studyRepository.findAllStudies(pageRequest);
    }

    // 단일 스터디 상세 조회
    public Study getStudy(Long id) {
        return studyRepository.findByIdWithLeader(id)
                .orElseThrow(() -> new IllegalArgumentException("Study not found with id: " + id));
    }



    // 스터디방 생성
    public Study createStudy(StudyCreateRequest request) {
        //1. 요청 데이터 검증
        validateRequest(request);

        //2. Study 엔티티 설정
        Study study = new Study();
        study.setTitle(request.getTitle());
        study.setDescription(request.getDescription());
        study.setMinCapacity(request.getMinCapacity());
        study.setMaxCapacity(request.getMaxCapacity());
        study.setStartAt(request.getStartAt());
        study.setEndAt(request.getEndAt());
        study.setIsOnline(request.getIsOnline());
        study.setType(request.getType());
//        study.setLeader(leader);

        // User 엔티티 로드 (leader)
        User leader = entityManager.find(User.class, 4L); // ID 1번 유저를 리더로 설정 test 할때
        // 현재 로그인한 사용자를 리더로 설정
        // User leader = getCurrentUser(); // Security Context에서 가져오거나 주입받아야 함 authService에서 메소드를 만들면 좋을꺼같아요!
        study.setLeader(leader);

        // 3. 오프라인 스터디인 경우 위치 정보 설정
        if (!request.getIsOnline() && request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));
            study.setLocation(location);
        }

        // 4. 저장
        Study savedStudy =  studyRepository.save(study);
        return studyRepository.findByIdWithLeader(savedStudy.getId())
                .orElseThrow(() -> new RuntimeException("Failed to load study with leader info"));
    }

    private void validateRequest(StudyCreateRequest request) {
        if (!request.getIsOnline() && request.getLocationId() == null) {
            throw new IllegalArgumentException("오프라인 스터디는 위치 정보가 필수입니다.");
        }

        if (request.getStartAt().isAfter(request.getEndAt())) {
            throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        if (request.getMinCapacity() > request.getMaxCapacity()) {
            throw new IllegalArgumentException("최소 인원은 최대 인원보다 클 수 없습니다.");
        }
    }


}
