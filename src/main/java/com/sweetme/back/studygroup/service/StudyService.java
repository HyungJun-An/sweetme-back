package com.sweetme.back.studygroup.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.studygroup.domain.Location;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.repository.LocationRepository;
import com.sweetme.back.studygroup.repository.StudyRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // User 엔티티 로드 (leader)
        User leader = entityManager.find(User.class, 2L); // ID 1번 유저를 리더로 설정 test 할때
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
