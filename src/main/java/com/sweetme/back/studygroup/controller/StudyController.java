package com.sweetme.back.studygroup.controller;

import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.dto.StudyDetailDTO;
import com.sweetme.back.studygroup.dto.StudySearchRequest;
import com.sweetme.back.studygroup.dto.StudyUpdateRequest;
import com.sweetme.back.studygroup.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    // 스터디 삭제
    @DeleteMapping("/{studyId}")
    public ResponseEntity<?> deleteStudy(@PathVariable Long studyId) {
        // 스터디 삭제 후 204 No Content 응답 반환
        studyService.deleteStudy(studyId);
        return ResponseEntity.noContent().build();
    }

    // 스터디 설정 수정
    @PutMapping("/{studyId}")
    public ResponseEntity<?> updateStudy(
            @PathVariable Long studyId,
            @RequestBody @Valid StudyUpdateRequest request
            ){
        Study updateStudy = studyService.updateStudy(studyId, request);
        return ResponseEntity.ok(StudyDetailDTO.from(updateStudy));
    }

    // 스터디 목록 조회 (검색/필터링)
    @GetMapping
    public ResponseEntity<Page<StudyDetailDTO>> getStudies(
            @RequestParam(defaultValue = "1") int page, // 1부터 시작
            @RequestParam(defaultValue = "10") int size
    ) {
        // 내부적으로는 0부터 시작하므로 1을 빼줌
        StudySearchRequest request = new StudySearchRequest();
        request.setPage(page - 1); // 1을 빼서 0부터 시작하게 변환
        request.setSize(size);

        Page<Study> studies = studyService.getStudies(request);
        Page<StudyDetailDTO> response = studies.map(StudyDetailDTO::from);
        return ResponseEntity.ok(response);
    }

    // 모든 스터디 조회
    @GetMapping("/all")
    public ResponseEntity<Page<StudyDetailDTO>> getAllStudies(
            @RequestParam(defaultValue = "1") int page, // 1부터 시작
            @RequestParam(defaultValue = "10") int size
    ) {
        // 내부적으로는 0부터 시작하므로 1을 빼줌
        StudySearchRequest request = new StudySearchRequest();
        request.setPage(page - 1); // 1을 빼서 0부터 시작하게 변환
        request.setSize(size);

        Page<Study> studies = studyService.getAllStudies(request);
        Page<StudyDetailDTO> response = studies.map(StudyDetailDTO::from);
        return ResponseEntity.ok(response);
    }

    // 스터디 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailDTO> getStudy(@PathVariable Long id) {
        Study study = studyService.getStudy(id);
        return ResponseEntity.ok(StudyDetailDTO.from(study));
    }


    // 스터디 생성
    // 시큐리티 없이 했을때
    @PostMapping
    public ResponseEntity<?> createStudy(@RequestBody @Valid StudyCreateRequest request ){// @Valid로 검증
        Study createdStudy = studyService.createStudy(request);
        return ResponseEntity.status(HttpStatus.CREATED) // 성공시 201
                .body(createdStudy);
    }

    // 시큐리티 활용해서
//    @PostMapping
//    public ResponseEntity<?> createStudy(
//            @RequestBody @Valid StudyCreateRequest request,
//            Authentication authentication // Spring Security에서 제공하는 현재 인증 정보
//    ){
//        // UserDetails에서 현재 로그인한 사용자 정보 가져오기
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        USer user = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        Study createdStudy = studyService.createStudy(request, user);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(createdStudy);
//    }

}
